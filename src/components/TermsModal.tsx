/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useEffect } from 'react';
import { X, FileText } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

interface TermsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const TermsModal: React.FC<TermsModalProps> = ({ isOpen, onClose }) => {
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
        <div className="px-6 py-5 border-b border-slate-200/80 flex items-center justify-between shrink-0 bg-slate-50/70">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-blue-100 flex items-center justify-center text-blue-600">
              <FileText className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-slate-950">
                Terms of Service
              </h3>
              <p className="text-xs text-slate-500">
                Color Sort Puzzle · Published 2026
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

        <div className="p-6 sm:p-8 overflow-y-auto space-y-4 text-xs sm:text-sm text-slate-600 leading-relaxed">
          <p>
            By installing or playing <strong>Color Sort Puzzle</strong>, you agree to these standard terms.
          </p>
          <h4 className="font-bold text-slate-900">License to Play</h4>
          <p>
            Color Sort Puzzle is provided free of charge for personal entertainment on Android devices. You may not reverse engineer, redistribute, or extract proprietary game code or art assets without express permission from Sinjarul Hoque.
          </p>
          <h4 className="font-bold text-slate-900">In-Game Content</h4>
          <p>
            Virtual currency (stars, coins) and unlocked glassware themes are entertainment items intended solely for gameplay progression with no real-world monetary value.
          </p>
          <h4 className="font-bold text-slate-900">Developer Contact</h4>
          <p>
            For support or questions, reach out to Sinjarul Hoque at <a href={`mailto:${GAME_INFO.developerEmail}`} className="text-purple-600 font-bold hover:underline">{GAME_INFO.developerEmail}</a>.
          </p>
        </div>

        <div className="px-6 py-4 border-t border-slate-200/80 bg-slate-50 flex items-center justify-end shrink-0">
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
