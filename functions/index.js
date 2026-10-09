const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore, FieldValue } = require("firebase-admin/firestore");

initializeApp();
const db = getFirestore();

// Reward configurations - can be moved to Remote Config in production
const REWARD_CONFIG = {
  coins: { amount: 25, limit: 5 },
  life: { amount: 1, limit: 3 },
  hint: { amount: 1, limit: 5 },
  undo: { amount: 1, limit: 5 },
  continue: { amount: 1, limit: 1 }, // 1 per level attempt
  bonus_reward: { amount: 50, limit: 1 }, // Double reward bonus
  daily_bonus: { amount: 100, limit: 1 } // 2x daily login bonus
};

exports.completeLevel = onCall(async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  const levelId = Number(request.data.levelId);
  const moves = Number(request.data.moves);
  const elapsedTime = Number(request.data.elapsedTime);
  const hintsUsed = Number(request.data.hintsUsed || 0);
  const undosUsed = Number(request.data.undosUsed || 0);
  if (!Number.isInteger(levelId) || levelId < 1 || levelId > 10000 || !Number.isInteger(moves) || moves < 1 || moves > 10000 || !Number.isInteger(elapsedTime) || elapsedTime < 0 || elapsedTime > 86400 || hintsUsed < 0 || undosUsed < 0) {
    throw new HttpsError("invalid-argument", "Invalid completion result.");
  }

  const uid = request.auth.uid;
  const userRef = db.collection("users").doc(uid);
  const progressRef = userRef.collection("levelProgress").doc(String(levelId));
  const config = await db.collection("levels").doc(String(levelId)).get();
  if (!config.exists || config.data().enabled === false) throw new HttpsError("failed-precondition", "Level unavailable.");
  const level = config.data();
  const optimalMoves = Math.max(1, Number(level.optimalMoves || moves));
  const stars = Math.max(1, Math.min(6, 6 - Math.floor(Math.max(0, moves - optimalMoves) / 3) - Math.min(4, Math.floor(elapsedTime / 30)) - (hintsUsed > 0 ? 1 : 0) - (undosUsed > 0 ? 1 : 0)));
  const baseCoins = Number(level.baseCoins || 25);
  const rewardCoins = baseCoins + [0, 0, 0, 10, 20, 35, 50][stars];
  const rewardXp = Number(level.baseXp || 100) + stars * 10;

  return db.runTransaction(async (transaction) => {
    const [userSnap, progressSnap] = await Promise.all([transaction.get(userRef), transaction.get(progressRef)]);
    const user = userSnap.exists ? userSnap.data() : {};
    const previous = progressSnap.exists ? progressSnap.data() : {};
    const alreadyWon = previous.completed === true;
    const improved = !alreadyWon || stars > Number(previous.bestStars || 0) || moves < Number(previous.bestMoves || Number.MAX_SAFE_INTEGER);
    const updates = {
      completed: true,
      bestStars: Math.max(stars, Number(previous.bestStars || 0)),
      bestMoves: Math.min(moves, Number(previous.bestMoves || Number.MAX_SAFE_INTEGER)),
      bestTime: Math.min(elapsedTime, Number(previous.bestTime || Number.MAX_SAFE_INTEGER)),
      attempts: Number(previous.attempts || 0) + 1,
      wins: Number(previous.wins || 0) + 1,
      updatedAt: FieldValue.serverTimestamp(),
      completedAt: previous.completedAt || FieldValue.serverTimestamp()
    };
    transaction.set(progressRef, updates, { merge: true });
    if (!alreadyWon) {
      transaction.set(userRef, {
        coins: Number(user.coins || 0) + rewardCoins,
        xp: Number(user.xp || 0) + rewardXp,
        playerLevel: Math.max(Number(user.playerLevel || 1), levelId + 1),
        updatedAt: FieldValue.serverTimestamp()
      }, { merge: true });
    }
    return { stars, rewardCoins: alreadyWon ? 0 : rewardCoins, rewardXp: alreadyWon ? 0 : rewardXp, improved };
  });
});

exports.claimDailyLoginReward = onCall(async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  const day = Number(request.data.day);
  const multiplier = Number(request.data.multiplier || 1); // Support 2x bonus
  if (!Number.isInteger(day) || day < 1 || day > 7) throw new HttpsError("invalid-argument", "Invalid reward day.");
  if (multiplier !== 1 && multiplier !== 2) throw new HttpsError("invalid-argument", "Invalid multiplier.");
  const uid = request.auth.uid;
  const stateRef = db.collection("users").doc(uid).collection("dailyLogin").doc("state");
  const rewardSnap = await db.collection("dailyLoginRewards").doc(String(day)).get();
  if (!rewardSnap.exists || rewardSnap.data().enabled === false) throw new HttpsError("failed-precondition", "Reward unavailable.");
  const reward = rewardSnap.data();
  return db.runTransaction(async (transaction) => {
    const stateSnap = await transaction.get(stateRef);
    const state = stateSnap.exists ? stateSnap.data() : {};
    const claimed = Array.isArray(state.claimedDays) ? state.claimedDays : [];
    if (claimed.includes(day) || Number(state.currentDay || 1) !== day) throw new HttpsError("already-exists", "Reward already claimed or locked.");
    // Check if multiplier bonus already used today
    const adRewardRef = db.collection("users").doc(uid).collection("adRewards");
    const today = new Date().toISOString().slice(0, 10);
    const bonusSnap = await adRewardRef.whereEqualTo("date", today).whereEqualTo("rewardType", "daily_bonus").get();
    if (multiplier === 2 && !bonusSnap.empty) throw new HttpsError("already-exists", "Daily bonus already claimed.");
    
    const userRef = db.collection("users").doc(uid);
    const userSnap = await transaction.get(userRef);
    const user = userSnap.exists ? userSnap.data() : {};
    const baseCoins = Number(reward.coins || 0);
    const finalCoins = baseCoins * multiplier;
    
    transaction.set(userRef, { coins: Number(user.coins || 0) + finalCoins, updatedAt: FieldValue.serverTimestamp() }, { merge: true });
    transaction.set(stateRef, { currentDay: day === 7 ? 1 : day + 1, claimedDays: [...claimed, day], updatedAt: FieldValue.serverTimestamp() }, { merge: true });
    
    // Record the bonus reward if 2x was used
    if (multiplier === 2) {
      transaction.set(adRewardRef.doc(), { rewardType: "daily_bonus", rewardAmount: baseCoins, adSessionId: request.data.adSessionId || "", date: today, claimedAt: FieldValue.serverTimestamp() });
    }
    
    return { day, coins: finalCoins, multiplier };
  });
});

exports.claimAdReward = onCall(async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  
  const rewardType = String(request.data.rewardType || "");
  const adSessionId = String(request.data.adSessionId || "");
  
  if (!REWARD_CONFIG[rewardType]) throw new HttpsError("invalid-argument", "Invalid reward type.");
  if (!adSessionId || adSessionId.length > 128) throw new HttpsError("invalid-argument", "Invalid ad session ID.");
  
  const uid = request.auth.uid;
  const config = REWARD_CONFIG[rewardType];
  const rewardRef = db.collection("users").doc(uid).collection("adRewards").doc(adSessionId);
  const userRef = db.collection("users").doc(uid);
  const day = new Date().toISOString().slice(0, 10);
  
  return db.runTransaction(async (transaction) => {
    // Check if this ad session was already claimed
    const existing = await transaction.get(rewardRef);
    if (existing.exists) throw new HttpsError("already-exists", "Ad reward already claimed.");
    
    // Check daily limit for this reward type
    const countSnap = await db.collection("users").doc(uid).collection("adRewards")
      .whereEqualTo("date", day)
      .whereEqualTo("rewardType", rewardType)
      .get();
    if (countSnap.size >= config.limit) throw new HttpsError("resource-exhausted", "Daily ad reward limit reached.");
    
    // For "continue" reward, also check level-specific limit
    if (rewardType === "continue") {
      const levelId = Number(request.data.levelId || 0);
      if (levelId > 0) {
        const continueSnap = await db.collection("users").doc(uid).collection("adRewards")
          .whereEqualTo("date", day)
          .whereEqualTo("rewardType", "continue")
          .whereEqualTo("levelId", levelId)
          .get();
        if (!continueSnap.empty) throw new HttpsError("resource-exhausted", "Continue already used for this level.");
      }
    }
    
    // For bonus_reward, check level completion
    if (rewardType === "bonus_reward") {
      const levelId = Number(request.data.levelId || 0);
      if (levelId > 0) {
        const bonusSnap = await db.collection("users").doc(uid).collection("adRewards")
          .whereEqualTo("date", day)
          .whereEqualTo("rewardType", "bonus_reward")
          .whereEqualTo("levelId", levelId)
          .get();
        if (!bonusSnap.empty) throw new HttpsError("resource-exhausted", "Bonus reward already claimed for this level.");
      }
    }
    
    const userSnap = await transaction.get(userRef);
    const user = userSnap.exists ? userSnap.data() : {};
    const amount = config.amount;
    
    let changes = {};
    switch (rewardType) {
      case "coins":
        changes = { coins: Number(user.coins || 0) + amount };
        break;
      case "life":
        changes = { lives: Math.min(5, Number(user.lives || 0) + amount) };
        break;
      case "hint":
        changes = { hints: Number(user.hints || 0) + amount };
        break;
      case "undo":
        changes = { undos: Number(user.undos || 0) + amount };
        break;
      case "continue":
        // Continue is handled client-side by restoring game state
        changes = {};
        break;
      case "bonus_reward":
        changes = { coins: Number(user.coins || 0) + amount };
        break;
      case "daily_bonus":
        changes = { coins: Number(user.coins || 0) + amount };
        break;
    }
    
    if (Object.keys(changes).length > 0) {
      transaction.set(userRef, { ...changes, updatedAt: FieldValue.serverTimestamp() }, { merge: true });
    }
    
    // Store ad reward record with all relevant data
    const rewardData = { 
      rewardType, 
      rewardAmount: amount, 
      adSessionId, 
      date: day, 
      claimedAt: FieldValue.serverTimestamp() 
    };
    
    // Add levelId for continue and bonus_reward
    if (["continue", "bonus_reward"].includes(rewardType)) {
      const levelId = Number(request.data.levelId || 0);
      if (levelId > 0) rewardData.levelId = levelId;
    }
    
    transaction.set(rewardRef, rewardData);
    return { rewardType, amount };
  });
});