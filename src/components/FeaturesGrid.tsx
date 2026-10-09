/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import {
  FlaskConical,
  Layers,
  RotateCcw,
  CheckCircle2,
  User,
  Trophy,
  CalendarCheck,
  Palette,
} from 'lucide-react';
import { GAME_INFO, GAME_FEATURES } from '../data/gameData';

const ICON_COMPONENTS = {
  FlaskConical,
  Layers,
  RotateCcw,
  CheckCircle2,
  User,
  Trophy,
  CalendarCheck,
  Palette,
};

export const FeaturesGrid: React.FC = () => {
  return (
    <section id="features" className="py-20 bg-white border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14 space-y-3">
          <div className="text-xs font-bold uppercase tracking-widest text-slate-500">
            Confirmed In-App Capabilities
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-950 tracking-tight text-balance">
            {GAME_INFO.featuresHeading}
          </h2>
          <p className="text-base text-slate-600">
            Every feature is verified in the published Android release of Color Sort Puzzle.
          </p>
        </div>

        {/* Features Responsive Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {GAME_FEATURES.map((feature) => {
            const Icon = ICON_COMPONENTS[feature.icon as keyof typeof ICON_COMPONENTS] || FlaskConical;

            return (
              <div
                key={feature.id}
                className="group p-6 rounded-2xl bg-white border border-slate-200/80 hover:border-slate-300 hover:shadow-md transition-all duration-200 flex flex-col justify-between"
              >
                <div>
                  {/* Icon & Category */}
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-10 h-10 rounded-xl bg-slate-50 group-hover:bg-orange-50 border border-slate-200/80 group-hover:border-orange-200/80 flex items-center justify-center transition-colors">
                      <Icon className="w-5 h-5 text-slate-700 group-hover:text-orange-600 transition-colors" />
                    </div>
                    <span className="text-[11px] font-semibold text-slate-400 group-hover:text-slate-600 transition-colors">
                      {feature.badge}
                    </span>
                  </div>

                  {/* Title */}
                  <h3 className="text-base font-bold text-slate-900 group-hover:text-orange-600 transition-colors mb-2 leading-snug">
                    {feature.title}
                  </h3>

                  {/* Short natural sentence */}
                  <p className="text-xs sm:text-sm text-slate-600 leading-relaxed font-normal">
                    {feature.summary}
                  </p>
                </div>

                <div className="pt-4 mt-4 border-t border-slate-100 flex items-center justify-between text-[11px] font-medium text-slate-400">
                  <span>Android App</span>
                  <span className="text-slate-300">·</span>
                  <span className="text-emerald-600 font-semibold">Included</span>
                </div>
              </div>
            );
          })}
        </div>

      </div>
    </section>
  );
};
