/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { Menu, X, ArrowUpRight } from 'lucide-react';
import { ColorfulLogo } from './ColorfulLogo';
import { GAME_INFO } from '../data/gameData';

interface NavbarSectionProps {
  onOpenFaq?: () => void;
}

export const NavbarSection: React.FC<NavbarSectionProps> = ({ onOpenFaq }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navLinks = [
    { label: 'Home', href: '#home', active: true },
    { label: 'Features', href: '#features' },
    { label: 'Screenshots', href: '#screenshots' },
    { label: 'How to Play', href: '#how-to-play' },
    { label: 'FAQ', href: '#faq', onClick: onOpenFaq },
  ];

  return (
    <header className="sticky top-0 z-50 w-full bg-white/95 backdrop-blur-md border-b border-slate-100 transition-all">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between gap-8">
        
        {/* Left: Brand Logo */}
        <a href="#home" className="shrink-0 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-purple-500 rounded-xl">
          <ColorfulLogo size="md" />
        </a>

        {/* Center: Clean Nav Links */}
        <nav className="hidden md:flex items-center gap-8 text-sm font-semibold text-slate-600">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              onClick={link.onClick}
              className={`transition-colors whitespace-nowrap hover:text-slate-950 ${
                link.active ? 'text-slate-950 font-bold' : ''
              }`}
            >
              {link.label}
            </a>
          ))}
        </nav>

        {/* Right: Primary Button */}
        <div className="hidden sm:flex items-center shrink-0">
          <a
            href={GAME_INFO.playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-2 px-5 py-2.5 text-sm font-bold text-white bg-gradient-to-r from-purple-600 via-indigo-600 to-purple-700 hover:from-purple-700 hover:to-indigo-700 active:scale-95 rounded-xl shadow-md shadow-purple-500/20 transition-all whitespace-nowrap"
          >
            {/* Google Play Icon */}
            <svg className="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none">
              <path d="M3.6 1.7L13.8 12 3.6 22.3c-.4-.4-.6-.9-.6-1.5V3.2c0-.6.2-1.1.6-1.5z" fill="#00C1DE"/>
              <path d="M17.2 8.6L13.8 12l3.4 3.4 3.9-2.2c1.1-.6 1.1-1.7 0-2.4l-3.9-2.2z" fill="#FFC900"/>
              <path d="M3.6 22.3L13.8 12l3.4 3.4-11.8 6.7c-.7.4-1.4.3-1.8.2z" fill="#FF3A44"/>
              <path d="M3.6 1.7C4 1.6 4.7 1.5 5.4 1.9L17.2 8.6 13.8 12 3.6 1.7z" fill="#00E676"/>
            </svg>
            <span>Get on Play Store</span>
          </a>
        </div>

        {/* Mobile toggle */}
        <div className="flex md:hidden items-center gap-2">
          <a
            href={GAME_INFO.playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="sm:hidden px-3 py-1.5 text-xs font-bold text-white bg-purple-600 rounded-lg"
          >
            Get App
          </a>
          <button
            type="button"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 text-slate-700 hover:bg-slate-100 rounded-lg"
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>

      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden border-b border-slate-200 bg-white px-4 py-4 space-y-2">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              onClick={() => {
                if (link.onClick) link.onClick();
                setMobileMenuOpen(false);
              }}
              className="block px-3 py-2 text-base font-semibold text-slate-700 hover:bg-slate-50 rounded-lg"
            >
              {link.label}
            </a>
          ))}
          <div className="pt-2">
            <a
              href={GAME_INFO.playStoreUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full flex items-center justify-center gap-2 py-3 bg-purple-600 text-white font-bold rounded-xl text-sm"
            >
              <span>Get on Play Store</span>
            </a>
          </div>
        </div>
      )}
    </header>
  );
};
