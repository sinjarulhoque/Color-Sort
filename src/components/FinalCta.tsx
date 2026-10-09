/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { ArrowUpRight, Smartphone, ShieldCheck, Sparkles } from 'lucide-react';
import { GAME_INFO, IMAGES } from '../data/gameData';

export const FinalCta: React.FC = () => {
  return (
    <section className="py-20 lg:py-28 bg-white border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Container with restrained orange and purple accents on predominantly white base */}
        <div className="relative rounded-3xl bg-gradient-to-br from-white via-orange-50/30 to-purple-50/40 border border-slate-200/90 shadow-xl shadow-slate-200/50 p-8 sm:p-12 lg:p-16 overflow-hidden">
          
          {/* Subtle decorative radial gradient glow */}
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-gradient-to-br from-orange-200/30 to-purple-200/30 rounded-full blur-3xl pointer-events-none" />
          
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-center relative z-10">
            
            {/* Left Column: CTA message and Button */}
            <div className="lg:col-span-7 space-y-6">
              
              <div className="inline-flex items-center gap-2 text-xs font-bold text-orange-600 uppercase tracking-widest">
                <Sparkles className="w-3.5 h-3.5" />
                <span>Start Your Puzzle Today</span>
              </div>

              <h2 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-slate-950 tracking-tight leading-tight text-balance">
                {GAME_INFO.finalCtaHeading}
              </h2>

              <p className="text-lg text-slate-600 max-w-xl font-normal leading-relaxed">
                {GAME_INFO.finalCtaCopy}
              </p>

              {/* Action Button */}
              <div className="pt-2 flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
                <a
                  href={GAME_INFO.playStoreUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center justify-center gap-3 px-8 py-4.5 text-base font-bold text-white bg-slate-950 hover:bg-slate-800 active:scale-[0.98] rounded-2xl shadow-lg shadow-slate-950/20 transition-all group focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-orange-500 whitespace-nowrap"
                >
                  {/* Google Play Triangle SVG */}
                  <svg className="w-5 h-5 shrink-0" viewBox="0 0 24 24" fill="none">
                    <path d="M3.6 1.7L13.8 12 3.6 22.3c-.4-.4-.6-.9-.6-1.5V3.2c0-.6.2-1.1.6-1.5z" fill="#00C1DE"/>
                    <path d="M17.2 8.6L13.8 12l3.4 3.4 3.9-2.2c1.1-.6 1.1-1.7 0-2.4l-3.9-2.2z" fill="#FFC900"/>
                    <path d="M3.6 22.3L13.8 12l3.4 3.4-11.8 6.7c-.7.4-1.4.3-1.8.2z" fill="#FF3A44"/>
                    <path d="M3.6 1.7C4 1.6 4.7 1.5 5.4 1.9L17.2 8.6 13.8 12 3.6 1.7z" fill="#00E676"/>
                  </svg>
                  <span>Get Color Sort Puzzle</span>
                  <ArrowUpRight className="w-4 h-4 text-orange-400 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform" />
                </a>
              </div>

              {/* Trust Indicators */}
              <div className="pt-2 flex items-center gap-5 text-xs text-slate-500">
                <div className="flex items-center gap-1.5 font-medium text-slate-700">
                  <Smartphone className="w-4 h-4 text-emerald-600" />
                  <span>Free on Google Play</span>
                </div>
                <span className="text-slate-300">·</span>
                <div className="flex items-center gap-1.5 font-medium text-slate-700">
                  <ShieldCheck className="w-4 h-4 text-blue-600" />
                  <span>Android 8.0+ Compatible</span>
                </div>
              </div>

            </div>

            {/* Right Column: Small gameplay preview beside CTA */}
            <div className="lg:col-span-5 flex justify-center lg:justify-end">
              <div className="relative w-full max-w-sm rounded-2xl overflow-hidden bg-white border border-slate-200/90 shadow-md p-3 group">
                <img
                  src={IMAGES.gameplaySort}
                  alt="Color Sort Puzzle in action preview"
                  referrerPolicy="no-referrer"
                  className="w-full h-auto rounded-xl object-cover transition-transform duration-300 group-hover:scale-[1.02]"
                />
                <div className="mt-3 px-1 flex items-center justify-between text-xs">
                  <span className="font-semibold text-slate-800">
                    Level Up Your Problem Solving
                  </span>
                  <span className="text-orange-600 font-bold">
                    Play Free →
                  </span>
                </div>
              </div>
            </div>

          </div>

        </div>

      </div>
    </section>
  );
};
