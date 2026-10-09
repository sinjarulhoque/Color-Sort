/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { Lightbulb, MousePointerClick, ArrowRight, CheckCircle2 } from 'lucide-react';

export const HowToPlaySection: React.FC = () => {
  return (
    <section id="how-to-play" className="py-20 bg-slate-50/70 border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Title */}
        <div className="mb-12">
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-950">
            <span className="text-purple-600">How to</span> Play
          </h2>
          <p className="text-sm text-slate-500 mt-1">
            Master the color sorting rules in three simple steps.
          </p>
        </div>

        {/* 4 Cards Row */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          
          {/* Card 1: Tap a tube */}
          <div className="p-6 rounded-3xl bg-white border border-slate-200/80 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              {/* Badge 1 */}
              <div className="w-8 h-8 rounded-full bg-purple-600 text-white font-black text-sm flex items-center justify-center mb-6 shadow-xs">
                1
              </div>

              {/* Graphic Illustration */}
              <div className="h-28 flex items-end justify-center gap-3 mb-6">
                <div className="w-6 h-20 rounded-b-xl border border-slate-300 bg-white flex flex-col-reverse p-0.5 opacity-60">
                  <div className="h-1/3 bg-blue-500 rounded-b-lg" />
                  <div className="h-1/3 bg-purple-500" />
                  <div className="h-1/3 bg-emerald-500" />
                </div>
                {/* Active lifted tube */}
                <div className="w-6 h-20 rounded-b-xl border-2 border-purple-500 bg-white flex flex-col-reverse p-0.5 -translate-y-3 shadow-md">
                  <div className="h-1/3 bg-amber-400 rounded-b-lg" />
                  <div className="h-1/3 bg-red-500" />
                  <div className="h-1/3 bg-sky-400 relative">
                    <MousePointerClick className="w-3.5 h-3.5 text-purple-600 absolute -top-4 left-1/2 -translate-x-1/2" />
                  </div>
                </div>
                <div className="w-6 h-20 rounded-b-xl border border-slate-300 bg-white flex flex-col-reverse p-0.5 opacity-60">
                  <div className="h-1/3 bg-orange-500 rounded-b-lg" />
                  <div className="h-1/3 bg-purple-500" />
                  <div className="h-1/3 bg-red-500" />
                </div>
              </div>

              {/* Text */}
              <h3 className="text-base font-bold text-slate-950 mb-1.5">
                Tap a tube
              </h3>
              <p className="text-xs text-slate-500 leading-relaxed font-normal">
                Select a tube with the top color you want to move.
              </p>
            </div>
          </div>

          {/* Card 2: Pour the color */}
          <div className="p-6 rounded-3xl bg-white border border-slate-200/80 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              {/* Badge 2 */}
              <div className="w-8 h-8 rounded-full bg-blue-500 text-white font-black text-sm flex items-center justify-center mb-6 shadow-xs">
                2
              </div>

              {/* Graphic Illustration */}
              <div className="h-28 flex items-center justify-center gap-3 mb-6">
                {/* Tilting pourer */}
                <div className="w-6 h-16 rounded-b-xl border-2 border-sky-400 bg-white flex flex-col-reverse p-0.5 rotate-25 -translate-y-2 shadow-sm">
                  <div className="h-1/2 bg-purple-500 rounded-b-lg" />
                  <div className="h-1/2 bg-sky-400" />
                </div>
                <ArrowRight className="w-4 h-4 text-sky-500" />
                {/* Receiving tube */}
                <div className="w-6 h-20 rounded-b-xl border-2 border-slate-400 bg-white flex flex-col-reverse p-0.5 shadow-sm">
                  <div className="h-1/4 bg-red-500 rounded-b-lg" />
                  <div className="h-1/4 bg-emerald-500" />
                  <div className="h-1/4 bg-sky-400" />
                  <div className="h-1/4 bg-sky-400 border-t border-dashed border-sky-300" />
                </div>
              </div>

              {/* Text */}
              <h3 className="text-base font-bold text-slate-950 mb-1.5">
                Pour the color
              </h3>
              <p className="text-xs text-slate-500 leading-relaxed font-normal">
                Tap another tube to pour the color.
              </p>
            </div>
          </div>

          {/* Card 3: Sort all colors */}
          <div className="p-6 rounded-3xl bg-white border border-slate-200/80 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              {/* Badge 3 */}
              <div className="w-8 h-8 rounded-full bg-emerald-500 text-white font-black text-sm flex items-center justify-center mb-6 shadow-xs">
                3
              </div>

              {/* Graphic Illustration */}
              <div className="h-28 flex items-end justify-center gap-3 mb-6">
                <div className="w-6 h-20 rounded-b-xl border-2 border-emerald-400 bg-white flex flex-col-reverse p-0.5 shadow-xs">
                  <div className="h-full bg-emerald-500 rounded-b-lg" />
                </div>
                <div className="w-6 h-20 rounded-b-xl border-2 border-rose-400 bg-white flex flex-col-reverse p-0.5 shadow-xs">
                  <div className="h-full bg-rose-500 rounded-b-lg" />
                </div>
                <div className="w-6 h-20 rounded-b-xl border-2 border-sky-400 bg-white flex flex-col-reverse p-0.5 shadow-xs">
                  <div className="h-full bg-sky-500 rounded-b-lg" />
                </div>
              </div>

              {/* Text */}
              <h3 className="text-base font-bold text-slate-950 mb-1.5">
                Sort all colors
              </h3>
              <p className="text-xs text-slate-500 leading-relaxed font-normal">
                Fill each tube with the same color to complete the level.
              </p>
            </div>
          </div>

          {/* Card 4: Simple Rules Endless Fun */}
          <div className="p-6 rounded-3xl bg-gradient-to-br from-purple-50 via-indigo-50/50 to-purple-50 border border-purple-200/70 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              {/* Lightbulb Icon */}
              <div className="w-12 h-12 rounded-2xl bg-amber-100 border border-amber-200/70 flex items-center justify-center mb-6 shadow-xs">
                <Lightbulb className="w-6 h-6 text-amber-600" />
              </div>

              <h3 className="text-base font-extrabold text-slate-950 mb-2 leading-snug">
                Simple Rules<br />Endless Fun
              </h3>

              <p className="text-xs text-slate-600 leading-relaxed font-normal">
                You can only pour the same color on top of another color or into an empty tube. Plan your moves carefully to win!
              </p>
            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
