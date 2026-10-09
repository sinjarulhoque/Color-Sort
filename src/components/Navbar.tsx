/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { Menu, X, ArrowUpRight } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

interface NavbarProps {
  onOpenPrivacy?: () => void;
}

export const Navbar: React.FC<NavbarProps> = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navLinks = [
    { label: 'Gameplay', href: '#gameplay' },
    { label: 'How to Play', href: '#how-to-play' },
    { label: 'Features', href: '#features' },
    { label: 'Why Play', href: '#why-play' },
    { label: 'Developer', href: '#developer' },
  ];

  return (
    <header className="sticky top-0 z-40 w-full bg-white/90 backdrop-blur-md border-b border-slate-200/80 transition-all">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-18 flex items-center justify-between gap-8">
        {/* Zone 1: Brand wordmark (single element line) */}
        <a
          href="#"
          className="flex items-center gap-2.5 text-slate-900 group whitespace-nowrap shrink-0 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-orange-500 rounded-lg p-1"
        >
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-purple-600 via-indigo-600 to-orange-500 flex items-center justify-center shadow-sm text-white font-black text-lg group-hover:scale-105 transition-transform">
            <span className="tracking-tighter">CS</span>
          </div>
          <span className="text-lg font-bold tracking-tight text-slate-900 group-hover:text-orange-600 transition-colors">
            Color Sort Puzzle
          </span>
        </a>

        {/* Zone 2: 4-5 clean single-line text navigation links */}
        <nav className="hidden md:flex items-center gap-7 text-sm font-medium text-slate-600">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              className="hover:text-slate-950 transition-colors whitespace-nowrap shrink-0 hover:underline underline-offset-4 decoration-orange-500/60"
            >
              {link.label}
            </a>
          ))}
        </nav>

        {/* Zone 3: 1 primary action */}
        <div className="hidden sm:flex items-center gap-3 shrink-0">
          <a
            href={GAME_INFO.playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-2 px-4.5 py-2.2 text-xs font-semibold text-white bg-slate-950 hover:bg-slate-800 active:scale-[0.98] rounded-xl transition-all shadow-sm shadow-slate-950/10 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-orange-500 whitespace-nowrap shrink-0"
          >
            <span>Google Play</span>
            <ArrowUpRight className="w-3.5 h-3.5 text-orange-400" />
          </a>
        </div>

        {/* Mobile menu button */}
        <div className="flex md:hidden items-center gap-2">
          <a
            href={GAME_INFO.playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="sm:hidden px-3 py-1.5 text-xs font-semibold text-white bg-slate-950 rounded-lg whitespace-nowrap"
          >
            Install
          </a>
          <button
            type="button"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-orange-500"
            aria-label="Toggle navigation menu"
            aria-expanded={mobileMenuOpen}
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile navigation panel */}
      {mobileMenuOpen && (
        <div className="md:hidden border-b border-slate-200 bg-white/98 backdrop-blur-lg px-4 pt-3 pb-5 space-y-1">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              onClick={() => setMobileMenuOpen(false)}
              className="block px-3 py-2.5 rounded-lg text-base font-medium text-slate-700 hover:text-slate-950 hover:bg-slate-50 transition-colors"
            >
              {link.label}
            </a>
          ))}
          <div className="pt-3 border-t border-slate-100">
            <a
              href={GAME_INFO.playStoreUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full flex items-center justify-center gap-2 px-4 py-3 text-sm font-semibold text-white bg-gradient-to-r from-slate-950 to-slate-900 rounded-xl shadow-sm"
            >
              <span>Download on Google Play</span>
              <ArrowUpRight className="w-4 h-4 text-orange-400" />
            </a>
          </div>
        </div>
      )}
    </header>
  );
};
