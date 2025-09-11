'use client';

import { useParams } from 'next/navigation';
import { motion } from 'framer-motion';
import { useLookup } from '@/hooks/useLookup';
import Header from '@/components/Header';
import LoadingSpinner from '@/components/LoadingSpinner';
import KpiCard from '@/components/KpiCard';
import RadialGauge from '@/components/RadialGauge';
import BarHorizontal from '@/components/BarHorizontal';
import DonutChart from '@/components/DonutChart';
import SparkLine from '@/components/SparkLine';
import ExportButton from '@/components/ExportButton';
import { LookupResponse } from '@/lib/api';

export default function DashboardPage() {
  const params = useParams();
  const lookupId = params.lookupId as string;
  const { data, error, isLoading } = useLookup(lookupId);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <Header />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <LoadingSpinner size="lg" className="mb-4" />
            <p className="text-gray-600 dark:text-gray-400">
              Analyzing website data...
            </p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <Header />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <div className="text-red-500 text-6xl mb-4">⚠️</div>
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
              Analysis Failed
            </h2>
            <p className="text-gray-600 dark:text-gray-400">
              {error?.message || 'Unable to load analysis data'}
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Header />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
            Website Analysis
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-400">
            {data.url}
          </p>
        </motion.div>

        {/* KPI Cards Row */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <KpiCard
            title="Estimated Visits/Month"
            value={data.traffic?.visits?.toLocaleString() || 'N/A'}
            subtitle="SimilarWeb data"
            icon="👥"
          />
          <KpiCard
            title="Global Rank"
            value={data.traffic?.rank?.toLocaleString() || 'N/A'}
            subtitle="Website ranking"
            icon="🏆"
          />
          <KpiCard
            title="Bounce Rate"
            value={`${data.traffic?.bounce?.toFixed(1) || 0}%`}
            subtitle="User engagement"
            icon="📈"
          />
          <KpiCard
            title="Avg Duration"
            value={`${Math.round((data.traffic?.avgDuration || 0) / 60)}m`}
            subtitle="Session length"
            icon="⏱️"
          />
        </div>

        {/* Charts Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Traffic by Country */}
          {data.traffic?.countries && data.traffic.countries.length > 0 && (
            <BarHorizontal
              data={data.traffic.countries.slice(0, 10).map(country => ({
                name: country.country,
                value: country.visits,
                color: '#3B82F6'
              }))}
              title="Traffic by Country"
            />
          )}

          {/* Device Distribution */}
          {data.traffic?.devices && data.traffic.devices.length > 0 && (
            <DonutChart
              data={data.traffic.devices.map(device => ({
                name: device.device,
                value: device.share,
                color: device.device === 'Desktop' ? '#3B82F6' : '#10B981'
              }))}
              title="Device Distribution"
            />
          )}
        </div>

        {/* Core Web Vitals */}
        {data.webVitals && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <RadialGauge
              value={data.webVitals.lcp || 0}
              max={4}
              label="Largest Contentful Paint"
              unit="s"
              size={120}
            />
            <RadialGauge
              value={data.webVitals.fid || 0}
              max={100}
              label="First Input Delay"
              unit="ms"
              size={120}
            />
            <RadialGauge
              value={data.webVitals.cls || 0}
              max={0.25}
              label="Cumulative Layout Shift"
              size={120}
            />
            <RadialGauge
              value={data.webVitals.score || 0}
              max={100}
              label="Performance Score"
              size={120}
            />
          </div>
        )}

        {/* SEO Analysis */}
        {data.seo && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
            <KpiCard
              title="Title Length"
              value={`${data.seo.title?.length || 0} chars`}
              subtitle={data.seo.title || 'No title'}
              icon="📝"
            />
            <KpiCard
              title="Readability Score"
              value={`${data.seo.readability?.toFixed(1) || 0}/100`}
              subtitle="Flesch Reading Ease"
              icon="📖"
            />
            <KpiCard
              title="Canonical URL"
              value={data.seo.canonical ? 'Yes' : 'No'}
              subtitle="SEO optimization"
              icon="🔗"
            />
          </div>
        )}

        {/* Technology Stack */}
        {data.tech && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <KpiCard
              title="IPv6 Support"
              value={data.tech.ipv6 ? 'Yes' : 'No'}
              subtitle="Modern networking"
              icon="🌐"
            />
            <KpiCard
              title="HTTP/2"
              value={data.tech.http2 ? 'Yes' : 'No'}
              subtitle="Protocol version"
              icon="⚡"
            />
            <KpiCard
              title="SSL Days Left"
              value={`${data.tech.sslDaysLeft || 0} days`}
              subtitle="Certificate expiry"
              icon="🔒"
            />
            <KpiCard
              title="Security Grade"
              value={data.tech.securityGrade || 'N/A'}
              subtitle="Overall security"
              icon="🛡️"
            />
          </div>
        )}

        {/* Live Traffic & Export */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {data.live && (
            <SparkLine
              data={[
                { value: data.live.trafficIndex, timestamp: new Date().toISOString() }
              ]}
              title="Live Traffic Index"
              value={data.live.trafficIndex}
              unit="/100"
              color="#10B981"
            />
          )}
          
          <ExportButton lookupId={lookupId} />
        </div>
      </main>
    </div>
  );
}
