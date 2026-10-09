/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { ArrowUpRight, Play, Sparkles, Smartphone, Check, ShieldCheck, Heart, Coins, Lightbulb, RotateCcw, Plus } from 'lucide-react';
import { GAME_INFO, IMAGES } from '../data/gameData';

export const Hero: React.FC = () => {
  const [activeView, setActiveView] = useState<'studio' | 'level12'>('studio');

  return (
    <section className="relative overflow-hidden pt-8 pb-16 lg:pt-16 lg:pb-24 bg-gradient-to-b from-white via-slate-50/50 to-white">
      {/* Subtle background ambient glow */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[700px] h-[500px] bg-gradient-to-tr from-orange-100/40 via-purple-100/30 to-blue-100/40 blur-3xl -z-10 pointer-events-none rounded-full" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          
          {/* Left Column: Editorial Headline & Conversion */}
          <div className="lg:col-span-6 space-y-6">
            
            {/* Small eyebrow label: “A COLORFUL PUZZLE EXPERIENCE” */}
            <div className="inline-flex items-center gap-2 text-xs font-bold tracking-widest text-orange-600 uppercase">
              <span className="w-2 h-2 rounded-full bg-orange-500" />
              <span>{GAME_INFO.eyebrow}</span>
            </div>

            {/* Main headline: “A Little Color. A Lot of Satisfaction.” */}
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-slate-950 tracking-tight leading-[1.08] text-balance">
              A Little Color.{' '}
              <span className="bg-gradient-to-r from-orange-600 via-purple-600 to-blue-600 bg-clip-text text-transparent">
                A Lot of Satisfaction.
              </span>
            </h1>

            {/* Supporting copy */}
            <p className="text-lg sm:text-xl text-slate-600 leading-relaxed max-w-xl text-pretty font-normal">
              {GAME_INFO.heroSubtitle}
            </p>

            {/* CTAs */}
            <div className="pt-2 flex flex-col sm:flex-row items-stretch sm:items-center gap-3.5">
              {/* Primary CTA */}
              <a
                href={GAME_INFO.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center justify-center gap-3 px-6 py-4 text-base font-semibold text-white bg-slate-950 hover:bg-slate-800 active:scale-[0.98] rounded-2xl shadow-lg shadow-slate-950/15 transition-all group focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-orange-500 whitespace-nowrap"
              >
                {/* Google Play Triangle SVG */}
                <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none">
                  <path d="M3.6 1.7L13.8 12 3.6 22.3c-.4-.4-.6-.9-.6-1.5V3.2c0-.6.2-1.1.6-1.5z" fill="#00C1DE"/>
                  <path d="M17.2 8.6L13.8 12l3.4 3.4 3.9-2.2c1.1-.6 1.1-1.7 0-2.4l-3.9-2.2z" fill="#FFC900"/>
                  <path d="M3.6 22.3L13.8 12l3.4 3.4-11.8 6.7c-.7.4-1.4.3-1.8.2z" fill="#FF3A44"/>
                  <path d="M3.6 1.7C4 1.6 4.7 1.5 5.4 1.9L17.2 8.6 13.8 12 3.6 1.7z" fill="#00E676"/>
                </svg>
                <span>Download on Google Play</span>
                <ArrowUpRight className="w-4 h-4 text-orange-400 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform" />
              </a>

              {/* Secondary CTA */}
              <a
                href="#gameplay"
                className="inline-flex items-center justify-center gap-2 px-5 py-4 text-base font-semibold text-slate-700 bg-white hover:bg-slate-50 hover:text-slate-950 border border-slate-200/90 rounded-2xl transition-all shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-400 whitespace-nowrap"
              >
                <span>Explore the Game</span>
                <Play className="w-4 h-4 text-slate-400 fill-slate-400" />
              </a>
            </div>

            {/* Small trust line: “Available on Android” */}
            <div className="pt-2 flex items-center gap-4 text-xs text-slate-500">
              <div className="flex items-center gap-1.5 font-medium text-slate-700">
                <Smartphone className="w-4 h-4 text-emerald-600" />
                <span>Available on Android</span>
              </div>
              <span className="text-slate-300">·</span>
              <div className="flex items-center gap-1.5 font-medium text-slate-700">
                <ShieldCheck className="w-4 h-4 text-blue-600" />
                <span>Free to Play</span>
              </div>
              <span className="text-slate-300">·</span>
              <span className="text-slate-600">By Sinjarul Hoque</span>
            </div>

            {/* Micro Highlights strip */}
            <div className="pt-4 grid grid-cols-3 gap-3 border-t border-slate-200/80">
              <div className="p-3 bg-white border border-slate-100 rounded-xl shadow-xs">
                <div className="text-xs text-slate-500 font-medium">Core Loop</div>
                <div className="text-sm font-bold text-slate-900 mt-0.5">Liquid Sorting</div>
              </div>
              <div className="p-3 bg-white border border-slate-100 rounded-xl shadow-xs">
                <div className="text-xs text-slate-500 font-medium">Stage Difficulty</div>
                <div className="text-sm font-bold text-slate-900 mt-0.5">Levels & Themes</div>
              </div>
              <div className="p-3 bg-white border border-slate-100 rounded-xl shadow-xs">
                <div className="text-xs text-slate-500 font-medium">Player Mode</div>
                <div className="text-sm font-bold text-slate-900 mt-0.5">Casual & Daily</div>
              </div>
            </div>

          </div>

          {/* Right Column: Prominent Device Showcase */}
          <div className="lg:col-span-6 relative">
            
            {/* View Switcher Controls */}
            <div className="flex items-center justify-between mb-3 px-1">
              <span className="text-xs font-semibold text-slate-500 tracking-wider uppercase">
                Gameplay Preview
              </span>
              <div className="inline-flex p-1 bg-slate-100/90 rounded-xl border border-slate-200/60 text-xs font-medium">
                <button
                  type="button"
                  onClick={() => setActiveView('studio')}
                  className={`px-3 py-1 rounded-lg transition-all ${
                    activeView === 'studio'
                      ? 'bg-white text-slate-900 shadow-xs font-semibold'
                      : 'text-slate-600 hover:text-slate-900'
                  }`}
                >
                  3D Studio Mockup
                </button>
                <button
                  type="button"
                  onClick={() => setActiveView('level12')}
                  className={`px-3 py-1 rounded-lg transition-all ${
                    activeView === 'level12'
                      ? 'bg-white text-slate-900 shadow-xs font-semibold'
                      : 'text-slate-600 hover:text-slate-900'
                  }`}
                >
                  Authentic Level 12 UI
                </button>
              </div>
            </div>

            {/* Display container */}
            <div className="relative rounded-3xl p-3 sm:p-5 bg-white border border-slate-200/80 shadow-xl shadow-slate-200/50 overflow-hidden">
              
              {activeView === 'studio' ? (
                /* Studio Render View */
                <div className="relative group">
                  <img
                    src={IMAGES.heroPhones}
                    alt="Color Sort Puzzle game mockup showing Android smartphones on tropical beach background"
                    referrerPolicy="no-referrer"
                    className="w-full h-auto rounded-2xl object-cover shadow-sm transition-transform duration-500 group-hover:scale-[1.01]"
                  />
                  <div className="absolute bottom-4 left-4 right-4 p-3.5 bg-slate-950/85 backdrop-blur-md rounded-xl text-white flex items-center justify-between border border-white/10 shadow-lg">
                    <div>
                      <div className="text-xs font-semibold text-orange-400 uppercase tracking-wide">
                        Android Game
                      </div>
                      <div className="text-sm font-bold text-white">
                        Color Sort Puzzle by Sinjarul Hoque
                      </div>
                    </div>
                    <a
                      href={GAME_INFO.playStoreUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="px-3 py-1.5 bg-orange-500 hover:bg-orange-600 text-white rounded-lg text-xs font-bold transition-colors shrink-0"
                    >
                      Get App
                    </a>
                  </div>
                </div>
              ) : (
                /* Authentic Game Screen View reproducing exact Level 12 UI from 889shots_so.png */
                <div className="bg-slate-900 rounded-2xl p-4 sm:p-6 text-white font-sans overflow-hidden relative">
                  
                  {/* Tropical Beach Sky & Background */}
                  <div className="rounded-xl overflow-hidden bg-gradient-to-b from-sky-400 via-sky-300 to-amber-100 p-4 border border-sky-200/30 shadow-inner relative min-h-[460px] flex flex-col justify-between">
                    
                    {/* Top Game Bar */}
                    <div className="flex items-center justify-between text-slate-900">
                      <div className="w-8 h-8 rounded-full bg-white/70 backdrop-blur-sm flex items-center justify-center font-bold text-slate-800 shadow-xs">
                        ←
                      </div>
                      <div className="bg-sky-500/80 backdrop-blur-md px-4 py-1 rounded-full text-white font-bold text-sm shadow-xs border border-white/30">
                        LEVEL 12
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="bg-amber-400/90 px-2.5 py-1 rounded-full text-slate-950 font-bold text-xs flex items-center gap-1 shadow-xs">
                          <Coins className="w-3.5 h-3.5 fill-slate-950" />
                          <span>1975</span>
                        </div>
                        <div className="w-8 h-8 rounded-full bg-white/70 flex items-center justify-center font-bold text-slate-800 text-xs">
                          ❚❚
                        </div>
                      </div>
                    </div>

                    {/* Stats & Tools Row */}
                    <div className="space-y-2 mt-2">
                      <div className="flex items-center justify-between text-xs font-semibold text-slate-800 px-1">
                        <span>MOVES: 0</span>
                        <div className="bg-white/80 px-2 py-0.5 rounded-full text-[11px] font-mono">
                          ⏱ 00:01
                        </div>
                        <span>LIVES: 12/5</span>
                      </div>

                      {/* Action buttons bar */}
                      <div className="flex items-center justify-center gap-2.5 pt-1">
                        <button className="px-3 py-1 bg-white/90 hover:bg-white text-slate-800 rounded-lg text-xs font-bold flex items-center gap-1 shadow-xs border border-slate-200">
                          <RotateCcw className="w-3.5 h-3.5 text-blue-600" />
                          <span>FREE</span>
                        </button>
                        <button className="px-3.5 py-1 bg-amber-400 hover:bg-amber-300 text-slate-950 rounded-lg text-xs font-bold flex items-center gap-1 shadow-xs">
                          <Lightbulb className="w-3.5 h-3.5 fill-amber-600 text-amber-600" />
                          <span>3</span>
                        </button>
                        <button className="px-3.5 py-1 bg-amber-500 hover:bg-amber-400 text-slate-950 rounded-lg text-xs font-bold flex items-center gap-1 shadow-xs">
                          <Plus className="w-3.5 h-3.5" />
                          <span>🧪 2</span>
                        </button>
                      </div>
                    </div>

                    {/* Tubes Area: Beach background with Level 12 tubes configuration */}
                    <div className="my-auto py-4">
                      {/* Top Row: 5 tubes */}
                      <div className="flex justify-center gap-2.5 mb-5">
                        {/* Tube 1: Purple, Green, Pink, Orange */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-purple-600 rounded-b-xl" />
                          <div className="h-1/4 bg-emerald-500" />
                          <div className="h-1/4 bg-pink-500" />
                          <div className="h-1/4 bg-orange-500 rounded-t-sm" />
                        </div>
                        {/* Tube 2: Blue, Red, Red, Orange */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-blue-600 rounded-b-xl" />
                          <div className="h-1/4 bg-red-600" />
                          <div className="h-1/4 bg-red-600" />
                          <div className="h-1/4 bg-orange-500 rounded-t-sm" />
                        </div>
                        {/* Tube 3: Yellow, Blue, Green, Purple */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-amber-400 rounded-b-xl" />
                          <div className="h-1/4 bg-blue-600" />
                          <div className="h-1/4 bg-emerald-500" />
                          <div className="h-1/4 bg-purple-600 rounded-t-sm" />
                        </div>
                        {/* Tube 4: Pink, Blue, Red, Green */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-pink-500 rounded-b-xl" />
                          <div className="h-1/4 bg-blue-600" />
                          <div className="h-1/4 bg-red-600" />
                          <div className="h-1/4 bg-emerald-500 rounded-t-sm" />
                        </div>
                        {/* Tube 5: Yellow, Yellow, Red, Pink */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-amber-400 rounded-b-xl" />
                          <div className="h-1/4 bg-amber-400" />
                          <div className="h-1/4 bg-red-600" />
                          <div className="h-1/4 bg-pink-500 rounded-t-sm" />
                        </div>
                      </div>

                      {/* Bottom Row: 3 tubes (2 mixed + 1 empty for pouring) */}
                      <div className="flex justify-center gap-3">
                        {/* Tube 6: Purple, Green, Yellow, Orange */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-purple-600 rounded-b-xl" />
                          <div className="h-1/4 bg-emerald-500" />
                          <div className="h-1/4 bg-amber-400" />
                          <div className="h-1/4 bg-orange-500 rounded-t-sm" />
                        </div>
                        {/* Tube 7: Blue, Red, Orange, Red */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-white/90 bg-white/20 backdrop-blur-xs flex flex-col-reverse p-0.5 overflow-hidden shadow-md">
                          <div className="h-1/4 bg-blue-600 rounded-b-xl" />
                          <div className="h-1/4 bg-red-600" />
                          <div className="h-1/4 bg-orange-500" />
                          <div className="h-1/4 bg-red-600 rounded-t-sm" />
                        </div>
                        {/* Tube 8: Empty Tube with dashed glass highlight */}
                        <div className="w-9 h-28 rounded-b-2xl border-2 border-dashed border-white/80 bg-white/10 backdrop-blur-xs flex items-center justify-center shadow-inner">
                          <span className="text-[10px] font-bold text-white/80 uppercase tracking-wider">Empty</span>
                        </div>
                      </div>
                    </div>

                    {/* Bottom Beach Sand Bar */}
                    <div className="text-center pt-2 text-[11px] font-medium text-amber-900/80">
                      🌴 Beach Level 12 · Think Ahead & Pour Liquid
                    </div>
                  </div>

                  {/* Caption underneath */}
                  <div className="mt-3 text-center text-xs text-slate-400">
                    Exact UI reproduction from the published Android build
                  </div>
                </div>
              )}

            </div>

            {/* Subtle decorative float label */}
            <div className="absolute -bottom-3 -right-2 hidden sm:flex items-center gap-2 px-3 py-2 bg-white rounded-xl border border-slate-200/90 shadow-md text-xs font-semibold text-slate-800">
              <span className="w-2 h-2 rounded-full bg-emerald-500" />
              <span>Google Play Verified</span>
            </div>

          </div>

        </div>
      </div>
    </section>
  );
};
