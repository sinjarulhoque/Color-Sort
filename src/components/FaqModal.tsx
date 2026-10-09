/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useEffect } from 'react';
import { X, HelpCircle, ChevronDown } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

interface FaqModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const FaqModal: React.FC<FaqModalProps> = ({ isOpen, onClose }) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.body.style.overflow = '';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const faqs = [
    {
      q: 'How do I play Color Sort Puzzle?',
      a: 'Tap any tube to select its topmost liquid layer, then tap an eligible recipient tube to pour. Liquid only transfers if the recipient tube is empty or if its topmost color matches the poured liquid.',
    },
    {
      q: 'Does Color Sort Puzzle work offline?',
      a: 'Yes! You can play Color Sort Puzzle offline anytime without an active internet connection. Your level progress and unlocked items are stored directly on your device.',
    },
    {
      q: 'Is there a time limit or penalty for wrong moves?',
      a: 'No time limits! Color Sort Puzzle is designed as a relaxing, stress-free brain exercise. You can take your time, rethink your moves, or use the Undo and Hint buttons whenever needed.',
    },
    {
      q: 'How can I unlock new themes and test tube shapes?',
      a: 'You earn coins and stars as you complete puzzle stages and daily challenges. You can use these in the in-game Themes and Shop menus to customize bottle shapes and vibrant liquid palettes.',
    },
    {
      q: 'Which Android versions are supported?',
      a: 'Color Sort Puzzle supports Android 8.0 (Oreo) and above on phones and tablets with smooth 60fps performance.',
    },
  ];

  return (
    <div
      role="dialog"
      aria-modal="true"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/60 backdrop-blur-xs animate-fade-in"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-xl max-h-[85vh] bg-white rounded-3xl border border-slate-200 shadow-2xl flex flex-col overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-6 py-5 border-b border-slate-200/80 flex items-center justify-between shrink-0 bg-slate-50/70">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-purple-100 flex items-center justify-center text-purple-600">
              <HelpCircle className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-slate-950">
                Frequently Asked Questions
              </h3>
              <p className="text-xs text-slate-500">
                Color Sort Puzzle Support & Answers
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Questions List */}
        <div className="p-6 sm:p-8 overflow-y-auto space-y-4 text-sm">
          {faqs.map((faq, idx) => (
            <div key={idx} className="p-4 rounded-2xl bg-slate-50 border border-slate-200/70 space-y-1.5">
              <h4 className="font-bold text-slate-900 text-sm">
                {faq.q}
              </h4>
              <p className="text-slate-600 text-xs sm:text-sm leading-relaxed">
                {faq.a}
              </p>
            </div>
          ))}
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-slate-200/80 bg-slate-50 flex items-center justify-between shrink-0 text-xs">
          <span className="text-slate-500">
            Need more help? Email <a href={`mailto:${GAME_INFO.developerEmail}`} className="text-purple-600 font-bold hover:underline">{GAME_INFO.developerEmail}</a>
          </span>
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold text-white bg-slate-950 hover:bg-slate-800 rounded-xl"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};
