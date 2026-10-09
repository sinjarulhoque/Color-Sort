/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';

interface ColorfulLogoProps {
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

export const ColorfulLogo: React.FC<ColorfulLogoProps> = ({ className = '', size = 'md' }) => {
  const iconSize = size === 'sm' ? 'w-8 h-8' : size === 'lg' ? 'w-12 h-12' : 'w-10 h-10';
  const textSize = size === 'sm' ? 'text-base' : size === 'lg' ? 'text-2xl' : 'text-xl';

  return (
    <div className={`flex items-center gap-2.5 font-bold tracking-tight select-none ${className}`}>
      {/* Icon: App icon with 3 test tubes inside rounded squircle */}
      <div className={`${iconSize} rounded-2xl bg-gradient-to-b from-indigo-950 via-slate-900 to-indigo-950 p-1.5 flex items-center justify-center gap-1 shadow-md border border-indigo-500/30 shrink-0`}>
        {/* Tube 1: Orange/Red liquid */}
        <div className="w-1.5 h-full rounded-b-full bg-slate-800/80 border border-white/20 flex flex-col-reverse p-[1px] overflow-hidden">
          <div className="h-2/3 bg-gradient-to-t from-orange-500 to-amber-400 rounded-b-full" />
        </div>
        {/* Tube 2: Blue/Cyan liquid */}
        <div className="w-1.5 h-full rounded-b-full bg-slate-800/80 border border-white/20 flex flex-col-reverse p-[1px] overflow-hidden">
          <div className="h-3/4 bg-gradient-to-t from-sky-500 to-cyan-300 rounded-b-full" />
        </div>
        {/* Tube 3: Purple/Pink liquid */}
        <div className="w-1.5 h-full rounded-b-full bg-slate-800/80 border border-white/20 flex flex-col-reverse p-[1px] overflow-hidden">
          <div className="h-1/2 bg-gradient-to-t from-purple-500 to-pink-500 rounded-b-full" />
        </div>
      </div>

      {/* Colorful text: "Color Sort Puzzle" */}
      <span className={`${textSize} font-extrabold flex items-center`}>
        <span className="text-purple-600">C</span>
        <span className="text-rose-500">o</span>
        <span className="text-amber-500">l</span>
        <span className="text-red-500">o</span>
        <span className="text-blue-500">r</span>
        <span className="text-slate-900 ml-1.5">Sort Puzzle</span>
      </span>
    </div>
  );
};
