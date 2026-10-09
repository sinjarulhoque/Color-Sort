/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { ArrowUpRight, Mail } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

interface FooterProps {
  onOpenPrivacy: () => void;
}

export const Footer: React.FC<FooterProps> = ({ onOpenPrivacy }) => {
  return (
    <footer className="bg-white border-t border-slate-200/80 pt-16 pb-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Main Footer Row */}
        <div className="grid grid-cols-1 md:grid-cols-12 gap-10 pb-12 border-b border-slate-100">
          
          {/* Brand & Description */}
          <div className="md:col-span-5 space-y-4">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-purple-600 via-indigo-600 to-orange-500 flex items-center justify-center shadow-xs text-white font-black text-sm">
                <span>CS</span>
              </div>
              <span className="text-lg font-bold tracking-tight text-slate-950">
                {GAME_INFO.title}
              </span>
            </div>

            {/* Short original one-line game description */}
            <p className="text-sm text-slate-600 leading-relaxed max-w-sm">
              A colorful liquid-sorting mobile puzzle game combining simple controls with rewarding spatial challenges on Android.
            </p>

            <div className="pt-1 text-xs text-slate-500">
              Developed by <span className="font-semibold text-slate-800">{GAME_INFO.developer}</span>
            </div>
          </div>

          {/* Navigation Links */}
          <div className="md:col-span-3 space-y-3">
            <div className="text-xs font-bold uppercase tracking-widest text-slate-400">
              Navigation
            </div>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="#" className="text-slate-600 hover:text-slate-950 transition-colors">
                  Home
                </a>
              </li>
              <li>
                <a href="#gameplay" className="text-slate-600 hover:text-slate-950 transition-colors">
                  Gameplay
                </a>
              </li>
              <li>
                <a href="#how-to-play" className="text-slate-600 hover:text-slate-950 transition-colors">
                  How to Play
                </a>
              </li>
              <li>
                <a href="#features" className="text-slate-600 hover:text-slate-950 transition-colors">
                  Features
                </a>
              </li>
              <li>
                <a href="#why-play" className="text-slate-600 hover:text-slate-950 transition-colors">
                  Why Play
                </a>
              </li>
              <li>
                <a href="#developer" className="text-slate-600 hover:text-slate-950 transition-colors">
                  Developer
                </a>
              </li>
            </ul>
          </div>

          {/* Download & Contact Column */}
          <div className="md:col-span-4 space-y-4">
            <div className="text-xs font-bold uppercase tracking-widest text-slate-400">
              Get the Game
            </div>

            <a
              href={GAME_INFO.playStoreUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 px-4 py-2.5 bg-slate-950 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold shadow-xs transition-colors"
            >
              <span>Download on Google Play</span>
              <ArrowUpRight className="w-3.5 h-3.5 text-orange-400" />
            </a>

            <div className="pt-2">
              <div className="text-xs font-semibold text-slate-400 mb-1">Inquiries & Support</div>
              <a
                href={`mailto:${GAME_INFO.developerEmail}`}
                className="inline-flex items-center gap-1.5 text-xs text-slate-700 hover:text-orange-600 font-medium transition-colors"
              >
                <Mail className="w-3.5 h-3.5 text-slate-400" />
                <span>{GAME_INFO.developerEmail}</span>
              </a>
            </div>
          </div>

        </div>

        {/* Bottom Bar */}
        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <div>
            {GAME_INFO.copyright}
          </div>

          <div className="flex items-center gap-6">
            <button
              type="button"
              onClick={onOpenPrivacy}
              className="hover:text-slate-900 transition-colors underline underline-offset-2"
            >
              Privacy Policy
            </button>
            <a
              href={GAME_INFO.playStoreUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-slate-900 transition-colors flex items-center gap-1"
            >
              <span>Google Play Store</span>
              <ArrowUpRight className="w-3 h-3 text-slate-400" />
            </a>
          </div>
        </div>

      </div>
    </footer>
  );
};
