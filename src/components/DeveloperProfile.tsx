/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { Mail, Check, Copy, UserCheck, Shield } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

export const DeveloperProfile: React.FC = () => {
  const [copied, setCopied] = useState(false);

  const handleCopyEmail = () => {
    navigator.clipboard.writeText(GAME_INFO.developerEmail);
    setCopied(true);
    setTimeout(() => setCopied(false), 2500);
  };

  return (
    <section id="developer" className="py-20 bg-slate-50/60 border-b border-slate-100">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="bg-white rounded-3xl border border-slate-200/80 shadow-md p-8 sm:p-12">
          <div className="space-y-6">
            
            {/* Header */}
            <div className="space-y-2">
              <div className="text-xs font-bold uppercase tracking-widest text-slate-500">
                Creator Profile
              </div>
              <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-950 tracking-tight">
                {GAME_INFO.developerHeading}
              </h2>
            </div>

            {/* Developer Details Card */}
            <div className="pt-2 flex flex-col sm:flex-row items-start sm:items-center gap-6">
              
              {/* Avatar Initial */}
              <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-purple-600 via-indigo-600 to-orange-500 flex items-center justify-center text-white font-extrabold text-2xl shadow-sm shrink-0">
                <span>SH</span>
              </div>

              {/* Name & Role */}
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <h3 className="text-xl font-bold text-slate-950">
                    {GAME_INFO.developer}
                  </h3>
                  <div className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200/60">
                    <UserCheck className="w-3 h-3" />
                    <span>Android Developer</span>
                  </div>
                </div>
                <div className="text-xs text-slate-500">
                  Creator of Color Sort Puzzle
                </div>
              </div>

            </div>

            {/* Bio Description from prompt */}
            <p className="text-base text-slate-700 leading-relaxed font-normal pt-2">
              {GAME_INFO.developerBio}
            </p>

            {/* Functional Contact Email */}
            <div className="pt-4 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <span className="text-xs font-semibold text-slate-400 block mb-1">
                  Developer Direct Contact
                </span>
                <a
                  href={`mailto:${GAME_INFO.developerEmail}`}
                  className="inline-flex items-center gap-2 text-sm sm:text-base font-bold text-slate-900 hover:text-orange-600 transition-colors"
                >
                  <Mail className="w-4 h-4 text-orange-500" />
                  <span>{GAME_INFO.developerEmail}</span>
                </a>
              </div>

              {/* Copy Email Button */}
              <button
                type="button"
                onClick={handleCopyEmail}
                className="inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 active:scale-95 transition-all self-start sm:self-auto"
              >
                {copied ? (
                  <>
                    <Check className="w-3.5 h-3.5 text-emerald-600" />
                    <span className="text-emerald-700">Email Copied</span>
                  </>
                ) : (
                  <>
                    <Copy className="w-3.5 h-3.5 text-slate-500" />
                    <span>Copy Email</span>
                  </>
                )}
              </button>
            </div>

            {/* Trust disclaimer */}
            <div className="pt-2 flex items-center gap-2 text-xs text-slate-400">
              <Shield className="w-3.5 h-3.5 text-slate-400 shrink-0" />
              <span>For player feedback, puzzle bug reports, or feature requests, contact directly via email.</span>
            </div>

          </div>
        </div>

      </div>
    </section>
  );
};
