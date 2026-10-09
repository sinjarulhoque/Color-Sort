/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { ArrowRight, Sparkles, Brain, FlaskConical, Palette, CheckCircle2 } from 'lucide-react';
import { GAME_INFO, IMAGES } from '../data/gameData';

export const GameplayShowcase: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'sort' | 'think' | 'complete' | 'themes'>('sort');

  const panels = [
    {
      id: 'sort',
      title: 'Sort',
      subtitle: 'Organize colorful liquids into matching tubes.',
      description:
        'Tap the source tube to pick up the top color layer, then tap an eligible recipient tube to pour. Watch liquid levels dynamically settle with smooth fluid motion.',
      badge: 'Action',
      icon: FlaskConical,
      image: IMAGES.gameplaySort,
      imageAlt: 'Color Sort liquid pouring between glass tubes',
      points: [
        'Precise liquid layer selection',
        'Fluid pouring animations and sound cues',
        'Intuitive one-finger controls',
      ],
    },
    {
      id: 'think',
      title: 'Think',
      subtitle: 'Plan your moves and work through increasingly challenging puzzles.',
      description:
        'With multiple mixed colors stacked in each bottle and restricted empty slots, success requires spatial foresight. Plan each cascade of moves to uncover buried layers.',
      badge: 'Strategy',
      icon: Brain,
      image: IMAGES.heroPhones,
      imageAlt: 'Beach theme Level 12 puzzle setup with 8 test tubes',
      points: [
        'Escalating color combinations',
        'Use buffer tubes strategically',
        'Undo moves freely when exploring new routes',
      ],
    },
    {
      id: 'complete',
      title: 'Complete',
      subtitle: 'Finish levels and celebrate your progress.',
      description:
        'Fill every glass container with a single, pure color to trigger victory fireworks, earn gold stars, and collect coins to unlock new custom glassware.',
      badge: 'Reward',
      icon: Sparkles,
      image: IMAGES.levelComplete,
      imageAlt: 'Victory screen showing perfectly sorted tubes and 3 stars',
      points: [
        'Three-star performance ratings',
        'Level completion bonuses and coins',
        'Instant progression to the next puzzle',
      ],
    },
    {
      id: 'themes',
      title: 'Customize',
      subtitle: 'Personalize tube shapes and liquid color palettes.',
      description:
        'Tailor the visual aesthetic to your preference with flared tubes, double-wall flasks, square vessels, and vibrant neon liquid palettes.',
      badge: 'Style',
      icon: Palette,
      image: IMAGES.themesCustom,
      imageAlt: 'Tube and liquid customization menu with distinctive glassware',
      points: [
        'Diverse glass vessel silhouettes',
        'Multiple vibrant color palettes',
        'Unlockable themes as you play',
      ],
    },
  ];

  const currentPanel = panels.find((p) => p.id === activeTab) || panels[0];

  return (
    <section className="py-20 bg-slate-50/60 border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-12 space-y-3">
          <div className="text-xs font-bold uppercase tracking-widest text-orange-600">
            Gameplay Showcase
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-950 tracking-tight text-balance">
            {GAME_INFO.showcaseHeading}
          </h2>
          <p className="text-base text-slate-600">
            Experience the three core phases of Color Sort Puzzle that make each stage so satisfying to resolve.
          </p>
        </div>

        {/* Tab Controls */}
        <div className="flex justify-center mb-10 overflow-x-auto pb-2">
          <div className="inline-flex p-1.5 bg-white rounded-2xl border border-slate-200/80 shadow-xs">
            {panels.map((p) => {
              const Icon = p.icon;
              const isActive = activeTab === p.id;
              return (
                <button
                  key={p.id}
                  type="button"
                  onClick={() => setActiveTab(p.id as any)}
                  className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all whitespace-nowrap ${
                    isActive
                      ? 'bg-slate-950 text-white shadow-xs'
                      : 'text-slate-600 hover:text-slate-950 hover:bg-slate-50'
                  }`}
                >
                  <Icon className={`w-4 h-4 ${isActive ? 'text-orange-400' : 'text-slate-400'}`} />
                  <span>{p.title}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Active Panel Content: Split layout */}
        <div className="bg-white rounded-3xl border border-slate-200/80 shadow-xl shadow-slate-200/50 p-6 sm:p-8 lg:p-12 overflow-hidden">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center">
            
            {/* Left: Detail description */}
            <div className="lg:col-span-5 space-y-6">
              <div className="space-y-3">
                <div className="inline-flex items-center gap-2 text-xs font-bold text-orange-600 uppercase tracking-widest">
                  <span>Phase {panels.findIndex((p) => p.id === activeTab) + 1}</span>
                  <span className="text-slate-300">·</span>
                  <span>{currentPanel.badge}</span>
                </div>
                
                <h3 className="text-2xl sm:text-3xl font-extrabold text-slate-950 tracking-tight">
                  {currentPanel.subtitle}
                </h3>

                <p className="text-base text-slate-600 leading-relaxed font-normal">
                  {currentPanel.description}
                </p>
              </div>

              {/* Verified Points */}
              <div className="space-y-3 pt-2">
                {currentPanel.points.map((pt) => (
                  <div key={pt} className="flex items-center gap-3 text-sm text-slate-700">
                    <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
                    <span>{pt}</span>
                  </div>
                ))}
              </div>

              {/* Action Link */}
              <div className="pt-4">
                <a
                  href={GAME_INFO.playStoreUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 text-sm font-bold text-orange-600 hover:text-orange-700 hover:underline"
                >
                  <span>Play {currentPanel.title} in Color Sort Puzzle</span>
                  <ArrowRight className="w-4 h-4" />
                </a>
              </div>
            </div>

            {/* Right: Media showcase */}
            <div className="lg:col-span-7">
              <div className="relative rounded-2xl overflow-hidden bg-slate-900 border border-slate-200 shadow-md aspect-4/3 flex items-center justify-center">
                <img
                  key={currentPanel.id}
                  src={currentPanel.image}
                  alt={currentPanel.imageAlt}
                  referrerPolicy="no-referrer"
                  className="w-full h-full object-cover transition-opacity duration-300"
                />
                
                {/* Scrim bar with caption */}
                <div className="absolute bottom-0 inset-x-0 p-4 bg-gradient-to-t from-slate-950/80 via-slate-950/40 to-transparent flex items-center justify-between text-white text-xs">
                  <span className="font-medium text-slate-200">
                    {currentPanel.imageAlt}
                  </span>
                  <span className="text-[11px] text-slate-400 font-mono">
                    Android Native
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
