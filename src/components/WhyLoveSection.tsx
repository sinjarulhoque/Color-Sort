/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { Trophy, Compass, BarChart3, Smile } from 'lucide-react';

export const WhyLoveSection: React.FC = () => {
  const cards = [
    {
      icon: <Trophy className="w-6 h-6 text-amber-500" />,
      bg: 'bg-amber-50 border-amber-200/60',
      title: 'Hundreds of Levels',
      desc: 'New and challenging puzzles to keep you engaged.',
    },
    {
      icon: <Compass className="w-6 h-6 text-emerald-600" />,
      bg: 'bg-emerald-50 border-emerald-200/60',
      title: 'Relaxing Experience',
      desc: 'No time limit. Play at your own pace.',
    },
    {
      icon: <BarChart3 className="w-6 h-6 text-purple-600" />,
      bg: 'bg-purple-50 border-purple-200/60',
      title: 'Improve Your Skills',
      desc: 'Boost memory, focus and problem-solving skills.',
    },
    {
      icon: <Smile className="w-6 h-6 text-orange-500" />,
      bg: 'bg-orange-50 border-orange-200/60',
      title: 'Fun for Everyone',
      desc: 'Perfect for all ages.',
    },
  ];

  return (
    <section id="why-love" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Title: Why You'll Love It */}
        <div className="mb-12">
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-950">
            Why You'll <span className="text-rose-500">Love</span>{' '}
            <span className="text-sky-500">It</span>
          </h2>
          <p className="text-sm text-slate-500 mt-1">
            Engineered for pure puzzle satisfaction and stress-free mental clarity.
          </p>
        </div>

        {/* 4 Cards Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {cards.map((card) => (
            <div
              key={card.title}
              className="p-8 rounded-3xl bg-slate-50/60 border border-slate-200/80 shadow-xs hover:shadow-md hover:bg-white hover:-translate-y-1 transition-all duration-300 flex flex-col items-center text-center group"
            >
              <div className={`w-12 h-12 rounded-2xl border flex items-center justify-center mb-6 shadow-xs ${card.bg} group-hover:scale-110 transition-transform duration-300`}>
                {card.icon}
              </div>
              <h3 className="text-base font-bold text-slate-950 mb-2">
                {card.title}
              </h3>
              <p className="text-xs sm:text-sm text-slate-500 leading-relaxed font-normal">
                {card.desc}
              </p>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
