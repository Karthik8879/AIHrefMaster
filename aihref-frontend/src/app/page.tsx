'use client';

import { useState } from 'react';
import { motion } from 'framer-motion';
import { useRouter } from 'next/navigation';
import { useAnalyse } from '@/hooks/useLookup';
import Header from '@/components/Header';
import LoadingSpinner from '@/components/LoadingSpinner';

export default function HomePage() {
  const [url, setUrl] = useState('');
  const { analyseUrl, isAnalyzing, error } = useAnalyse();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!url.trim()) return;

    const lookupId = await analyseUrl(url);
    if (lookupId) {
      router.push(`/dashboard/${lookupId}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Header />
      
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-center"
        >
          <h1 className="text-4xl md:text-6xl font-bold text-gray-900 dark:text-white mb-6">
            Analyze Any Website
          </h1>
          <p className="text-xl text-gray-600 dark:text-gray-400 mb-8 max-w-2xl mx-auto">
            Get comprehensive insights about website traffic, performance, SEO, and technology stack - completely free.
          </p>

          <motion.form
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
            onSubmit={handleSubmit}
            className="max-w-2xl mx-auto"
          >
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1">
                <input
                  type="url"
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  placeholder="Enter any URL (e.g., https://openai.com)"
                  className="w-full px-4 py-3 text-lg border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-800 dark:text-white placeholder-gray-500 dark:placeholder-gray-400"
                  disabled={isAnalyzing}
                />
              </div>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                disabled={isAnalyzing || !url.trim()}
                className="px-8 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold rounded-lg transition-colors duration-200 flex items-center justify-center"
              >
                {isAnalyzing ? (
                  <>
                    <LoadingSpinner size="sm" className="mr-2" />
                    Analyzing...
                  </>
                ) : (
                  'Analyze'
                )}
              </motion.button>
            </div>
            
            {error && (
              <motion.p
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-red-600 dark:text-red-400 mt-4 text-sm"
              >
                {error}
              </motion.p>
            )}
          </motion.form>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.4 }}
            className="mt-16 grid grid-cols-1 md:grid-cols-3 gap-8"
          >
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">📊</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                Traffic Analytics
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                Get estimated monthly visits, global rank, and traffic sources
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-green-100 dark:bg-green-900 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">⚡</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                Performance
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                Core Web Vitals, PageSpeed scores, and performance insights
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-purple-100 dark:bg-purple-900 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl">🔍</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                SEO & Tech
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                SEO analysis, technology stack, and security assessment
              </p>
            </div>
          </motion.div>
        </motion.div>
      </main>
    </div>
  );
}