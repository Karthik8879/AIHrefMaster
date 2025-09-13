'use client'

import { useParams } from 'next/navigation'
import { useEffect, useState } from 'react'
import { ArrowLeft, RefreshCw, Download, Globe, BarChart3 } from 'lucide-react'
import Link from 'next/link'
import { MetricCard } from '@/components/MetricCard'
import { TrafficChart } from '@/components/TrafficChart'
import { PerformanceGauge } from '@/components/PerformanceGauge'
import { TechnologyStack } from '@/components/TechnologyStack'
import { SeoMetrics } from '@/components/SeoMetrics'
import { SecurityStatus } from '@/components/SecurityStatus'
import { LoadingSpinner } from '@/components/LoadingSpinner'

interface LookupData {
  id: string
  url: string
  requestedAt: string
  expiresAt: string
  traffic: {
    visits: number
    rank: number
    bounce: number
    avgDuration: number
    countries: Array<{ country: string, share: number, visits: number }>
    devices: Array<{ device: string, share: number }>
    referrers: Array<{ site: string, share: number }>
    searchShare: number
    socialShare: number
  }
  webVitals: {
    lcp: number
    fid: number
    cls: number
    score: number
    screenshotB64: string | null
  }
  seo: {
    title: string
    description: string
    h1: string
    wordCount: number
    readability: number
    canonical: boolean
  }
  tech: {
    ipv6: boolean
    http2: boolean
    sslDaysLeft: number
    securityGrade: string
  } | null
  live: {
    trafficIndex: number
    lastUpdated: string
  }
  threat: {
    isSafe: boolean
    threats: string[]
  } | null
}

export default function DashboardPage() {
  const params = useParams()
  const lookupId = params.lookupId as string
  const [data, setData] = useState<LookupData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`/api/lookup/${lookupId}`)
        if (!response.ok) {
          throw new Error('Failed to fetch data')
        }
        const result = await response.json()
        setData(result)
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred')
      } finally {
        setLoading(false)
      }
    }

    if (lookupId) {
      fetchData()
    }
  }, [lookupId])

  const handleRefresh = () => {
    setLoading(true)
    setError(null)
    // Re-fetch data
    window.location.reload()
  }

  const handleExport = async () => {
    try {
      const response = await fetch(`/api/export/${lookupId}`)
      if (response.ok) {
        const blob = await response.blob()
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `aihref-analysis-${lookupId}.pdf`
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
      }
    } catch (error) {
      console.error('Export failed:', error)
    }
  }

  if (loading) {
    return <LoadingSpinner />
  }

  if (error || !data) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Analysis Not Found
          </h2>
          <p className="text-gray-600 dark:text-gray-300 mb-8">
            {error || 'The requested analysis could not be found.'}
          </p>
          <Link
            href="/"
            className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Home
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Header */}
      <header className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <Link
                href="/"
                className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
              >
                <ArrowLeft className="h-5 w-5" />
              </Link>
              <div>
                <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
                  {data.url}
                </h1>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Analysis completed on {new Date(data.requestedAt).toLocaleDateString()}
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <button
                onClick={handleRefresh}
                className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
              >
                <RefreshCw className="h-5 w-5" />
              </button>
              <button
                onClick={handleExport}
                className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
              >
                <Download className="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {/* Key Metrics */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <MetricCard
            title="Monthly Visits"
            value={data.traffic?.visits?.toLocaleString() || '0'}
            icon={<BarChart3 className="h-5 w-5" />}
            trend="+12%"
          />
          <MetricCard
            title="Bounce Rate"
            value={`${data.traffic?.bounce || 0}%`}
            icon={<Globe className="h-5 w-5" />}
            trend="-5%"
          />
          <MetricCard
            title="Avg. Visit Duration"
            value={`${Math.round((data.traffic?.avgDuration || 0) / 60)}m`}
            icon={<BarChart3 className="h-5 w-5" />}
            trend="+8%"
          />
          <MetricCard
            title="Global Rank"
            value={data.traffic?.rank?.toLocaleString() || 'N/A'}
            icon={<BarChart3 className="h-5 w-5" />}
            trend="+3%"
          />
        </div>

        {/* Charts and Analysis */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          <TrafficChart data={data.traffic} />
          <PerformanceGauge data={data.webVitals} />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <TechnologyStack data={data.tech} />
          <SeoMetrics data={data.seo} />
          <SecurityStatus data={data.threat} />
        </div>
      </main>
    </div>
  )
}