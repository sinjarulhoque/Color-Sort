/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useEffect } from 'react';
import { X, Shield, Lock, Eye, Mail } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

interface PrivacyModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const PrivacyModal: React.FC<PrivacyModalProps> = ({ isOpen, onClose }) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.body.style.overflow = '';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="privacy-policy-title"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/60 backdrop-blur-xs animate-fade-in"
    >
      <div
        className="relative w-full max-w-2xl max-h-[85vh] bg-white rounded-3xl border border-slate-200 shadow-2xl flex flex-col overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-6 py-5 border-b border-slate-200/80 flex items-center justify-between shrink-0 bg-slate-50/70">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-orange-100 flex items-center justify-center text-orange-600">
              <Shield className="w-4 h-4" />
            </div>
            <div>
              <h3 id="privacy-policy-title" className="text-lg font-bold text-slate-950">
                Privacy Policy
              </h3>
              <p className="text-xs text-slate-500">
                Color Sort Puzzle · Last updated March 2026
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors"
            aria-label="Close dialog"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Content */}
        <div className="p-6 sm:p-8 overflow-y-auto space-y-6 text-sm text-slate-700 leading-relaxed">
          
          <div className="p-4 rounded-2xl bg-orange-50/60 border border-orange-200/60 text-xs text-orange-900 flex items-start gap-3">
            <Lock className="w-4 h-4 text-orange-600 shrink-0 mt-0.5" />
            <span>
              <strong>Summary:</strong> Color Sort Puzzle by Sinjarul Hoque does not collect or sell your personal information. Game progress is saved locally on your Android device.
            </span>
          </div>

          <section className="space-y-2">
            <h4 className="text-base font-bold text-slate-950">1. Information Collection & Storage</h4>
            <p>
              <strong>Color Sort Puzzle</strong> (Package: <code className="text-xs bg-slate-100 px-1.5 py-0.5 rounded font-mono">{GAME_INFO.packageName}</code>) operates as an offline-friendly puzzle game. Your game state—including level progression, earned coins, unlocked tube themes, and achievement milestones—is stored locally on your device via Android shared preferences.
            </p>
          </section>

          <section className="space-y-2">
            <h4 className="text-base font-bold text-slate-950">2. Third-Party Services</h4>
            <p>
              The application utilizes standard Google Play Services for game delivery, updates, and crash reporting. These services may collect standard anonymous hardware diagnostics, operating system versions, and anonymized advertising IDs in accordance with Google Play Developer policies:
            </p>
            <ul className="list-disc pl-5 space-y-1 text-slate-600">
              <li>
                <a
                  href="https://policies.google.com/privacy"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-orange-600 hover:underline"
                >
                  Google Play Services Privacy Policy
                </a>
              </li>
            </ul>
          </section>

          <section className="space-y-2">
            <h4 className="text-base font-bold text-slate-950">3. Children's Privacy</h4>
            <p>
              Color Sort Puzzle is designed as a family-friendly casual game suitable for all ages. We do not knowingly solicit or collect personal information from children under the age of 13.
            </p>
          </section>

          <section className="space-y-2">
            <h4 className="text-base font-bold text-slate-950">4. Data Deletion</h4>
            <p>
              You may reset or delete all game data at any time by clearing the application storage via your device settings (<code className="text-xs bg-slate-100 px-1.5 py-0.5 rounded font-mono">Settings &gt; Apps &gt; Color Sort Puzzle &gt; Storage &gt; Clear Data</code>) or by uninstalling the application.
            </p>
          </section>

          <section className="space-y-2">
            <h4 className="text-base font-bold text-slate-950">5. Contact Information</h4>
            <p>
              If you have any questions or feedback regarding this Privacy Policy, please contact the developer directly:
            </p>
            <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center gap-2 font-mono text-xs text-slate-800">
              <Mail className="w-4 h-4 text-orange-500" />
              <span>{GAME_INFO.developerEmail}</span>
            </div>
          </section>

        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-slate-200/80 bg-slate-50 flex items-center justify-end shrink-0">
          <button
            type="button"
            onClick={onClose}
            className="px-5 py-2 text-xs font-semibold text-white bg-slate-950 hover:bg-slate-800 rounded-xl transition-colors"
          >
            Understood
          </button>
        </div>

      </div>
    </div>
  );
};
