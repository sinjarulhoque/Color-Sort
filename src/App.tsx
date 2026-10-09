/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { NavbarSection } from './components/NavbarSection';
import { HeroSection } from './components/HeroSection';
import { FeatureBadgesRow } from './components/FeatureBadgesRow';
import { ScreenshotsSection } from './components/ScreenshotsSection';
import { HowToPlaySection } from './components/HowToPlaySection';
import { WhyLoveSection } from './components/WhyLoveSection';
import { DownloadBanner } from './components/DownloadBanner';
import { FooterSection } from './components/FooterSection';
import { TrailerModal } from './components/TrailerModal';
import { PrivacyModal } from './components/PrivacyModal';
import { FaqModal } from './components/FaqModal';
import { TermsModal } from './components/TermsModal';

export default function App() {
  const [trailerModalOpen, setTrailerModalOpen] = useState(false);
  const [privacyModalOpen, setPrivacyModalOpen] = useState(false);
  const [faqModalOpen, setFaqModalOpen] = useState(false);
  const [termsModalOpen, setTermsModalOpen] = useState(false);

  return (
    <div className="min-h-screen bg-white text-slate-900 selection:bg-purple-600 selection:text-white flex flex-col font-sans">
      {/* 1. Header Navigation */}
      <NavbarSection onOpenFaq={() => setFaqModalOpen(true)} />

      {/* Main Sections flow matching reference image identically */}
      <main className="flex-1">
        {/* 2. Hero Section */}
        <HeroSection onWatchTrailer={() => setTrailerModalOpen(true)} />

        {/* 3. 5 Feature Badges Row */}
        <FeatureBadgesRow />

        {/* 4. Game Screenshots Gallery */}
        <ScreenshotsSection />

        {/* 5. How to Play (1, 2, 3 + Simple Rules card) */}
        <HowToPlaySection />

        {/* 6. Why You'll Love It (4 cards) */}
        <WhyLoveSection />

        {/* 7. Purple Download Banner CTA */}
        <DownloadBanner />
      </main>

      {/* 8. Footer */}
      <FooterSection
        onOpenPrivacy={() => setPrivacyModalOpen(true)}
        onOpenFaq={() => setFaqModalOpen(true)}
        onOpenTerms={() => setTermsModalOpen(true)}
      />

      {/* Modals */}
      <TrailerModal
        isOpen={trailerModalOpen}
        onClose={() => setTrailerModalOpen(false)}
      />

      <PrivacyModal
        isOpen={privacyModalOpen}
        onClose={() => setPrivacyModalOpen(false)}
      />

      <FaqModal
        isOpen={faqModalOpen}
        onClose={() => setFaqModalOpen(false)}
      />

      <TermsModal
        isOpen={termsModalOpen}
        onClose={() => setTermsModalOpen(false)}
      />
    </div>
  );
}
