/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { ArrowDownToLine, Star, Users, Play, ShieldCheck, Sparkles } from 'lucide-react';
import { GAME_INFO, HERO_ASSET } from '../data/gameData';

interface HeroSectionProps {
  onWatchTrailer?: () => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({ onWatchTrailer }) => {
  return (
    <section id="home" className="relative pt-10 pb-16 lg:pt-16 lg:pb-24 overflow-hidden bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          
          {/* Left Column: Eyebrow, Title, Copy, CTAs, Stats */}
          <div className="lg:col-span-6 space-y-6 animate-fade-in-up">
            
            {/* Pill Tag */}
            <div>
              <span className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full text-xs font-bold tracking-wider text-purple-700 bg-purple-50 border border-purple-200/60 uppercase">
                <Sparkles className="w-3.5 h-3.5 text-purple-600" />
                <span>{GAME_INFO.eyebrow}</span>
              </span>
            </div>

            {/* Headline with exact colorful styling */}
            <h1 className="text-5xl sm:text-6xl lg:text-7xl font-black tracking-tight leading-[1.05]">
              <span className="inline-block">
                <span className="text-purple-600">C</span>
                <span className="text-rose-500">o</span>
                <span className="text-amber-500">l</span>
                <span className="text-red-500">o</span>
                <span className="text-blue-500">r</span>
              </span>{' '}
              <span className="text-purple-600">Sort</span>
              <br />
              <span className="text-slate-950">Puzzle</span>
            </h1>

            {/* Supporting Copy */}
            <p className="text-base sm:text-lg text-slate-600 leading-relaxed max-w-lg font-normal">
              {GAME_INFO.heroSubtitle}
            </p>

            {/* CTAs Row */}
            <div className="pt-2 flex flex-wrap items-center gap-4">
              {/* Primary CTA: Purple Google Play Button */}
              <a
                href={GAME_INFO.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-3 px-6 py-3.5 rounded-2xl text-sm font-bold text-white bg-gradient-to-r from-purple-600 via-indigo-600 to-purple-700 hover:from-purple-700 hover:to-indigo-700 active:scale-95 shadow-lg shadow-purple-500/25 transition-all whitespace-nowrap"
              >
                {/* Google Play Triangle SVG */}
                <svg className="w-5 h-5 shrink-0" viewBox="0 0 24 24" fill="none">
                  <path d="M3.6 1.7L13.8 12 3.6 22.3c-.4-.4-.6-.9-.6-1.5V3.2c0-.6.2-1.1.6-1.5z" fill="#00C1DE"/>
                  <path d="M17.2 8.6L13.8 12l3.4 3.4 3.9-2.2c1.1-.6 1.1-1.7 0-2.4l-3.9-2.2z" fill="#FFC900"/>
                  <path d="M3.6 22.3L13.8 12l3.4 3.4-11.8 6.7c-.7.4-1.4.3-1.8.2z" fill="#FF3A44"/>
                  <path d="M3.6 1.7C4 1.6 4.7 1.5 5.4 1.9L17.2 8.6 13.8 12 3.6 1.7z" fill="#00E676"/>
                </svg>
                <span>Get it on Google Play</span>
              </a>

              {/* Secondary CTA: Watch Trailer */}
              <button
                type="button"
                onClick={onWatchTrailer}
                className="inline-flex items-center gap-2.5 px-6 py-3.5 rounded-2xl text-sm font-bold text-slate-800 bg-white hover:bg-slate-50 active:scale-95 border border-slate-200/90 shadow-xs transition-all whitespace-nowrap"
              >
                <div className="w-6 h-6 rounded-full bg-slate-100 flex items-center justify-center">
                  <Play className="w-3 h-3 text-slate-700 fill-slate-700 ml-0.5" />
                </div>
                <span>Watch Trailer</span>
              </button>
            </div>

            {/* 3 Stats Strip */}
            <div className="pt-6 grid grid-cols-3 gap-4 max-w-md border-t border-slate-100">
              {/* Stat 1 */}
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-blue-50 flex items-center justify-center text-blue-600 shrink-0">
                  <ArrowDownToLine className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-base font-extrabold text-slate-950 leading-tight">100K+</div>
                  <div className="text-xs text-slate-500 font-medium">Downloads</div>
                </div>
              </div>

              {/* Stat 2 */}
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-amber-50 flex items-center justify-center text-amber-500 shrink-0">
                  <Star className="w-5 h-5 fill-amber-500" />
                </div>
                <div>
                  <div className="text-base font-extrabold text-slate-950 leading-tight flex items-center gap-1">
                    <span>4.6</span>
                    <Star className="w-3.5 h-3.5 fill-amber-500 text-amber-500 inline" />
                  </div>
                  <div className="text-xs text-slate-500 font-medium">User Rating</div>
                </div>
              </div>

              {/* Stat 3 */}
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-purple-50 flex items-center justify-center text-purple-600 shrink-0">
                  <Users className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-base font-extrabold text-slate-950 leading-tight">All Ages</div>
                  <div className="text-xs text-slate-500 font-medium">Family Friendly</div>
                </div>
              </div>
            </div>

          </div>

          {/* Right Column: Clean Hero Image 889shots_so.png without background wrapper */}
          <div className="lg:col-span-6 flex items-center justify-center py-2">
            <div className="relative w-full max-w-xl flex items-center justify-center transition-transform duration-500 hover:scale-[1.02]">
              <img
                src={HERO_ASSET}
                alt="Color Sort Puzzle actual mobile gameplay and home menu on Android smartphones"
                className="w-full h-auto max-h-[540px] object-contain block drop-shadow-lg"
              />
            </div>
          </div>

        </div>
      </div>
    </section>
  );
};
