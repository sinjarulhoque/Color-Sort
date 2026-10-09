/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { Target, Sparkles, Clock, TrendingUp } from 'lucide-react';
import { GAME_INFO, WHY_PLAY_POINTS } from '../data/gameData';

export const WhyPlay: React.FC = () => {
  const icons = [
    <Target className="w-6 h-6 text-orange-600" key="target" />,
    <Sparkles className="w-6 h-6 text-purple-600" key="sparkles" />,
    <Clock className="w-6 h-6 text-blue-600" key="clock" />,
    <TrendingUp className="w-6 h-6 text-emerald-600" key="progress" />,
  ];

  return (
    <section id="why-play" className="py-24 bg-white border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="max-w-3xl mb-16 space-y-4">
          <div className="text-xs font-bold uppercase tracking-widest text-slate-500">
            Player Experience
          </div>
          <h2 className="text-3xl sm:text-5xl font-extrabold text-slate-950 tracking-tight leading-tight">
            {GAME_INFO.whyPlayHeading}
          </h2>
          <p className="text-lg text-slate-600 leading-relaxed font-normal">
            Designed from the ground up for thoughtful, uncluttered puzzle sessions without intrusive mechanics.
          </p>
        </div>

        {/* Spacious Editorial 2x2 Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 lg:gap-12">
          {WHY_PLAY_POINTS.map((item, idx) => (
            <div
              key={item.title}
              className="p-8 sm:p-10 rounded-3xl bg-slate-50/60 border border-slate-200/80 hover:bg-white hover:border-slate-300 hover:shadow-lg transition-all duration-300 flex flex-col justify-between"
            >
              <div>
                <div className="w-12 h-12 rounded-2xl bg-white border border-slate-200 flex items-center justify-center shadow-xs mb-6">
                  {icons[idx]}
                </div>
                
                <h3 className="text-xl sm:text-2xl font-bold text-slate-950 mb-3 leading-snug">
                  {item.title}
                </h3>

                <p className="text-slate-600 leading-relaxed text-sm sm:text-base font-normal">
                  {item.description}
                </p>
              </div>

              <div className="pt-6 mt-6 border-t border-slate-200/60 flex items-center justify-between text-xs font-medium text-slate-400">
                <span>Color Sort Experience</span>
                <span className="text-slate-900 font-mono font-semibold">0{idx + 1}</span>
              </div>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
