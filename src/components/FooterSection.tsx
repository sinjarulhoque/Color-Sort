/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { Mail, Youtube, Facebook, Instagram, Share2 } from 'lucide-react';
import { ColorfulLogo } from './ColorfulLogo';
import { GAME_INFO } from '../data/gameData';

interface FooterSectionProps {
  onOpenPrivacy: () => void;
  onOpenFaq: () => void;
  onOpenTerms: () => void;
}

export const FooterSection: React.FC<FooterSectionProps> = ({
  onOpenPrivacy,
  onOpenFaq,
  onOpenTerms,
}) => {
  return (
    <footer className="bg-white border-t border-slate-100 pt-16 pb-10 text-slate-600 text-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* 4 Columns Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-10 pb-16">
          
          {/* Column 1: App branding & socials (lg:col-span-4) */}
          <div className="lg:col-span-4 space-y-4">
            <ColorfulLogo size="md" />

            <p className="text-xs sm:text-sm text-slate-500 leading-relaxed max-w-sm">
              A fun and relaxing puzzle game for everyone. Sort the colors, solve levels, and enjoy the challenge!
            </p>

            {/* Social Icons using clean Lucide icons */}
            <div className="flex items-center gap-2.5 pt-2">
              <a
                href={GAME_INFO.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-950 flex items-center justify-center transition-colors"
                title="Google Play"
                aria-label="Google Play"
              >
                <Youtube className="w-4 h-4" />
              </a>
              <a
                href={GAME_INFO.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-950 flex items-center justify-center transition-colors"
                title="Facebook"
                aria-label="Facebook"
              >
                <Facebook className="w-4 h-4" />
              </a>
              <a
                href={GAME_INFO.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-950 flex items-center justify-center transition-colors"
                title="Instagram"
                aria-label="Instagram"
              >
                <Instagram className="w-4 h-4" />
              </a>
              <a
                href={GAME_INFO.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-950 flex items-center justify-center transition-colors"
                title="Share"
                aria-label="Share"
              >
                <Share2 className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Column 2: Quick Links (lg:col-span-2) */}
          <div className="lg:col-span-2 space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900">
              Quick Links
            </h4>
            <ul className="space-y-2 text-xs">
              <li>
                <a href="#home" className="hover:text-slate-950 transition-colors">
                  Home
                </a>
              </li>
              <li>
                <a href="#features" className="hover:text-slate-950 transition-colors">
                  Features
                </a>
              </li>
              <li>
                <a href="#screenshots" className="hover:text-slate-950 transition-colors">
                  Screenshots
                </a>
              </li>
              <li>
                <button
                  type="button"
                  onClick={onOpenFaq}
                  className="hover:text-slate-950 transition-colors text-left"
                >
                  FAQ
                </button>
              </li>
              <li>
                <a href={`mailto:${GAME_INFO.developerEmail}`} className="hover:text-slate-950 transition-colors">
                  Contact
                </a>
              </li>
            </ul>
          </div>

          {/* Column 3: Support (lg:col-span-3) */}
          <div className="lg:col-span-3 space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900">
              Support
            </h4>
            <ul className="space-y-2 text-xs">
              <li>
                <button
                  type="button"
                  onClick={onOpenFaq}
                  className="hover:text-slate-950 transition-colors text-left"
                >
                  Help Center
                </button>
              </li>
              <li>
                <button
                  type="button"
                  onClick={onOpenPrivacy}
                  className="hover:text-slate-950 transition-colors text-left"
                >
                  Privacy Policy
                </button>
              </li>
              <li>
                <button
                  type="button"
                  onClick={onOpenTerms}
                  className="hover:text-slate-950 transition-colors text-left"
                >
                  Terms of Service
                </button>
              </li>
            </ul>
          </div>

          {/* Column 4: Developer (lg:col-span-3) */}
          <div className="lg:col-span-3 space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900">
              Developer
            </h4>
            <div className="text-sm font-bold text-slate-950">
              {GAME_INFO.developer}
            </div>
            <div>
              <a
                href={`mailto:${GAME_INFO.developerEmail}`}
                className="inline-flex items-center gap-1.5 text-xs text-slate-600 hover:text-purple-600 transition-colors"
              >
                <Mail className="w-3.5 h-3.5 text-slate-400" />
                <span>{GAME_INFO.developerEmail}</span>
              </a>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed font-normal">
              Independent developer creating simple and fun mobile apps.
            </p>
          </div>

        </div>

        {/* Bottom Sub-bar */}
        <div className="pt-8 border-t border-slate-100 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-400">
          <div>
            © 2026 Color Sort Puzzle. All rights reserved.
          </div>
          <div>
            Developed by <span className="font-semibold text-slate-700">Sinjarul Hoque</span>
          </div>
        </div>

      </div>
    </footer>
  );
};
