/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { ArrowRight, Check } from 'lucide-react';
import { GAME_INFO, HOW_TO_PLAY_STEPS } from '../data/gameData';

export const HowToPlay: React.FC = () => {
  return (
    <section id="how-to-play" className="py-20 bg-slate-50/70 border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14 space-y-3">
          <div className="text-xs font-bold uppercase tracking-widest text-orange-600">
            Rules of the Game
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-950 tracking-tight text-balance">
            {GAME_INFO.howToPlayHeading}
          </h2>
          <p className="text-base text-slate-600">
            Master the simple logic of Color Sort Puzzle in three straightforward moves.
          </p>
        </div>

        {/* 3 Numbered Steps Grid: Horizontal on Desktop, Vertical on Mobile */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {HOW_TO_PLAY_STEPS.map((step, idx) => (
            <div
              key={step.step}
              className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative flex flex-col justify-between"
            >
              <div>
                {/* Step Number & Connector */}
                <div className="flex items-center justify-between mb-6">
                  <span className="text-3xl font-extrabold font-mono text-orange-500 tracking-tight">
                    {step.step}
                  </span>
                  <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                    Step {idx + 1}
                  </span>
                </div>

                {/* Step Title */}
                <h3 className="text-xl font-bold text-slate-950 mb-2">
                  {step.title}
                </h3>

                {/* Step Main Instruction */}
                <p className="text-sm text-slate-600 leading-relaxed mb-6">
                  {step.description}
                </p>

                {/* Visual Graphic Representation */}
                <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-center my-4 min-h-[140px]">
                  {idx === 0 && (
                    /* Diagram 1: Selecting a tube (tube lifted with selected arrow) */
                    <div className="flex items-end gap-3">
                      <div className="w-8 h-24 rounded-b-xl border border-slate-300 bg-white flex flex-col-reverse p-0.5 opacity-60">
                        <div className="h-1/3 bg-blue-500 rounded-b-lg" />
                        <div className="h-1/3 bg-purple-500" />
                        <div className="h-1/3 bg-emerald-500" />
                      </div>
                      {/* Active lifted tube */}
                      <div className="w-8 h-24 rounded-b-xl border-2 border-orange-500 bg-white flex flex-col-reverse p-0.5 -translate-y-3 shadow-md">
                        <div className="h-1/3 bg-orange-500 rounded-b-lg" />
                        <div className="h-1/3 bg-red-500" />
                        <div className="h-1/3 bg-sky-400 relative">
                          <span className="absolute -top-4 left-1/2 -translate-x-1/2 text-orange-600 font-bold text-[10px]">Tap</span>
                        </div>
                      </div>
                      <div className="w-8 h-24 rounded-b-xl border border-slate-300 bg-white flex flex-col-reverse p-0.5 opacity-60">
                        <div className="h-1/3 bg-amber-400 rounded-b-lg" />
                        <div className="h-1/3 bg-purple-500" />
                        <div className="h-1/3 bg-red-500" />
                      </div>
                    </div>
                  )}

                  {idx === 1 && (
                    /* Diagram 2: Pouring liquid from one tube to another matching top tube */
                    <div className="flex items-center gap-4">
                      {/* Tilting pourer */}
                      <div className="w-8 h-20 rounded-b-xl border-2 border-sky-400 bg-white flex flex-col-reverse p-0.5 rotate-12 -translate-y-2 shadow-sm">
                        <div className="h-1/2 bg-purple-500 rounded-b-lg" />
                        <div className="h-1/2 bg-sky-400" />
                      </div>
                      <ArrowRight className="w-4 h-4 text-orange-500 shrink-0" />
                      {/* Target tube receiving matching sky blue */}
                      <div className="w-8 h-24 rounded-b-xl border-2 border-slate-400 bg-white flex flex-col-reverse p-0.5 shadow-sm">
                        <div className="h-1/4 bg-red-500 rounded-b-lg" />
                        <div className="h-1/4 bg-emerald-500" />
                        <div className="h-1/4 bg-sky-400" />
                        <div className="h-1/4 bg-sky-400 border-t border-dashed border-sky-300" />
                      </div>
                    </div>
                  )}

                  {idx === 2 && (
                    /* Diagram 3: Completely sorted uniform tubes */
                    <div className="flex items-end gap-3">
                      <div className="w-8 h-24 rounded-b-xl border-2 border-emerald-400 bg-white flex flex-col-reverse p-0.5 shadow-xs">
                        <div className="h-full bg-emerald-500 rounded-b-lg" />
                      </div>
                      <div className="w-8 h-24 rounded-b-xl border-2 border-orange-400 bg-white flex flex-col-reverse p-0.5 shadow-xs">
                        <div className="h-full bg-orange-500 rounded-b-lg" />
                      </div>
                      <div className="w-8 h-24 rounded-b-xl border-2 border-purple-400 bg-white flex flex-col-reverse p-0.5 shadow-xs">
                        <div className="h-full bg-purple-600 rounded-b-lg" />
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* Verified Rule Tip */}
              <div className="pt-3 border-t border-slate-100 flex items-start gap-2 text-xs text-slate-500">
                <Check className="w-3.5 h-3.5 text-orange-600 shrink-0 mt-0.5" />
                <span>{step.tip}</span>
              </div>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
