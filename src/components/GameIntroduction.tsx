/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { MousePointerClick, Brain, Sparkles, Check } from 'lucide-react';
import { GAME_INFO, IMAGES, THREE_BENEFITS } from '../data/gameData';

export const GameIntroduction: React.FC = () => {
  const benefitIcons = [
    <MousePointerClick className="w-5 h-5 text-orange-600" key="controls" />,
    <Brain className="w-5 h-5 text-purple-600" key="planning" />,
    <Sparkles className="w-5 h-5 text-blue-600" key="progress" />,
  ];

  return (
    <section id="gameplay" className="py-20 bg-white border-y border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-16 items-center">
          
          {/* Left Column: Image with realistic phone showcase */}
          <div className="lg:col-span-6 order-2 lg:order-1">
            <div className="relative mx-auto max-w-md lg:max-w-none">
              
              {/* Outer container */}
              <div className="relative rounded-3xl overflow-hidden bg-slate-50 border border-slate-200/90 shadow-xl shadow-slate-200/60 p-3 sm:p-4">
                <img
                  src={IMAGES.gameplaySort}
                  alt="Color Sort Puzzle close-up gameplay showing colorful liquid transfer between glass test tubes"
                  referrerPolicy="no-referrer"
                  className="w-full h-auto rounded-2xl object-cover"
                />
                
                {/* Floating tip box */}
                <div className="absolute bottom-6 left-6 right-6 p-4 bg-white/95 backdrop-blur-md rounded-2xl border border-slate-200/80 shadow-md">
                  <div className="flex items-center gap-2 text-xs font-bold text-orange-600 uppercase tracking-wider mb-1">
                    <span>Sorting Principle</span>
                  </div>
                  <p className="text-xs sm:text-sm text-slate-700 font-medium">
                    Pour liquid only into matching top colors or an empty buffer tube.
                  </p>
                </div>
              </div>

            </div>
          </div>

          {/* Right Column: Heading, Core Gameplay copy & 3 Concise Benefits */}
          <div className="lg:col-span-6 order-1 lg:order-2 space-y-8">
            
            <div className="space-y-4">
              <div className="text-xs font-bold uppercase tracking-widest text-slate-500">
                Core Mechanics
              </div>
              
              <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-950 tracking-tight leading-tight">
                {GAME_INFO.gameIntroHeading}
              </h2>

              <p className="text-base sm:text-lg text-slate-600 leading-relaxed font-normal">
                {GAME_INFO.gameIntroCopy}
              </p>
            </div>

            {/* Three concise benefits list */}
            <div className="space-y-4 pt-2">
              {THREE_BENEFITS.map((benefit, idx) => (
                <div
                  key={benefit.title}
                  className="flex items-start gap-4 p-4 rounded-2xl bg-slate-50/70 border border-slate-200/70 hover:bg-slate-50 hover:border-slate-300 transition-colors"
                >
                  <div className="w-10 h-10 rounded-xl bg-white border border-slate-200 flex items-center justify-center shrink-0 shadow-xs">
                    {benefitIcons[idx]}
                  </div>
                  <div className="space-y-1">
                    <h3 className="text-base font-bold text-slate-900">
                      {benefit.title}
                    </h3>
                    <p className="text-sm text-slate-600 leading-relaxed">
                      {benefit.description}
                    </p>
                  </div>
                </div>
              ))}
            </div>

            {/* In-app action note */}
            <div className="pt-2 flex items-center gap-3 text-xs text-slate-500">
              <div className="w-5 h-5 rounded-full bg-emerald-100 flex items-center justify-center shrink-0">
                <Check className="w-3.5 h-3.5 text-emerald-700" />
              </div>
              <span>No penalty timers. Solve each puzzle at your natural pace.</span>
            </div>

          </div>

        </div>

      </div>
    </section>
  );
};
