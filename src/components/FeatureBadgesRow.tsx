/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { Brain, MousePointerClick, Trophy, Palette, WifiOff } from 'lucide-react';

export const FeatureBadgesRow: React.FC = () => {
  const cards = [
    {
      icon: <Brain className="w-6 h-6 text-indigo-600" />,
      bg: 'bg-indigo-50 border-indigo-100',
      title: 'Train Your Brain',
      desc: 'Improve focus and logical thinking',
    },
    {
      icon: <MousePointerClick className="w-6 h-6 text-sky-600" />,
      bg: 'bg-sky-50 border-sky-100',
      title: 'Easy to Play',
      desc: 'Simple controls for everyone',
    },
    {
      icon: <Trophy className="w-6 h-6 text-amber-600" />,
      bg: 'bg-amber-50 border-amber-100',
      title: 'Challenging Levels',
      desc: 'Hundreds of unique puzzles',
    },
    {
      icon: <Palette className="w-6 h-6 text-purple-600" />,
      bg: 'bg-purple-50 border-purple-100',
      title: 'Beautiful Themes',
      desc: 'Colorful and relaxing designs',
    },
    {
      icon: <WifiOff className="w-6 h-6 text-rose-600" />,
      bg: 'bg-rose-50 border-rose-100',
      title: 'Play Anytime',
      desc: 'Works offline anywhere',
    },
  ];

  return (
    <section id="features" className="py-14 bg-slate-50/70 border-y border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 sm:gap-6">
          {cards.map((card, idx) => (
            <div
              key={card.title}
              className={`p-6 rounded-2xl bg-white border border-slate-200/80 shadow-xs hover:shadow-md hover:-translate-y-1 transition-all duration-300 flex flex-col items-center text-center group ${
                idx === 4 ? 'col-span-2 md:col-span-1' : ''
              }`}
            >
              <div className={`w-12 h-12 rounded-xl border flex items-center justify-center mb-4 ${card.bg} group-hover:scale-110 transition-transform duration-300`}>
                {card.icon}
              </div>
              <h3 className="text-sm font-bold text-slate-900 mb-1 leading-snug">
                {card.title}
              </h3>
              <p className="text-xs text-slate-500 leading-relaxed font-normal">
                {card.desc}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
