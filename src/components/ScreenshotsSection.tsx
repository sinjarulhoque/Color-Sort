/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useRef } from 'react';
import { ArrowLeft, ArrowRight } from 'lucide-react';
import { SCREENSHOT_ASSETS } from '../data/gameData';

export const ScreenshotsSection: React.FC = () => {
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  const scroll = (direction: 'left' | 'right') => {
    if (scrollContainerRef.current) {
      const scrollAmount = 320;
      scrollContainerRef.current.scrollBy({
        left: direction === 'left' ? -scrollAmount : scrollAmount,
        behavior: 'smooth',
      });
    }
  };

  return (
    <section id="screenshots" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header with Left Title & Right Arrow Buttons */}
        <div className="flex items-center justify-between mb-12">
          <div>
            <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-950">
              <span className="text-purple-600">Game</span> Screenshots
            </h2>
            <p className="text-sm text-slate-500 mt-1">
              Explore authentic in-game captures from Color Sort Puzzle on Android.
            </p>
          </div>

          {/* Prev / Next Arrow Circle Buttons */}
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => scroll('left')}
              className="w-10 h-10 rounded-full border border-slate-200 bg-white hover:bg-slate-50 active:scale-95 flex items-center justify-center text-slate-700 hover:text-slate-950 transition-all shadow-xs"
              aria-label="Scroll left"
            >
              <ArrowLeft className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={() => scroll('right')}
              className="w-10 h-10 rounded-full border border-slate-200 bg-white hover:bg-slate-50 active:scale-95 flex items-center justify-center text-slate-700 hover:text-slate-950 transition-all shadow-xs"
              aria-label="Scroll right"
            >
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Horizontal Scrollable Carousel for all 5 Screenshots */}
        <div
          ref={scrollContainerRef}
          className="flex gap-6 overflow-x-auto pb-6 scrollbar-none snap-x snap-mandatory"
        >
          {SCREENSHOT_ASSETS.map((item, idx) => (
            <div
              key={idx}
              className="shrink-0 w-[260px] sm:w-[280px] snap-center flex flex-col items-center text-center group"
            >
              {/* Clean Screenshot Card without thick mobile frame */}
              <div className="w-full h-[520px] rounded-3xl overflow-hidden border border-slate-200/90 shadow-md mb-4 bg-white transition-all duration-300 group-hover:shadow-xl group-hover:-translate-y-1">
                <img
                  src={item.src}
                  alt={item.title}
                  className="w-full h-full object-cover object-top block"
                />
              </div>

              {/* Title & Caption below */}
              <h3 className="text-base font-bold text-slate-950 mb-1">
                {item.title}
              </h3>
              <p className="text-xs text-slate-500 font-normal">
                {item.desc}
              </p>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
