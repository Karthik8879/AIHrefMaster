interface SeoData {
  title: string
  description: string
  h1: string
  wordCount: number
  readability: number
  canonical: boolean
}

interface SeoMetricsProps {
  data: SeoData
}

export function SeoMetrics({ data }: SeoMetricsProps) {
  const getReadabilityColor = (score: number) => {
    if (score >= 80) return 'text-green-600 dark:text-green-400'
    if (score >= 60) return 'text-yellow-600 dark:text-yellow-400'
    return 'text-red-600 dark:text-red-400'
  }

  const getReadabilityBgColor = (score: number) => {
    if (score >= 80) return 'bg-green-100 dark:bg-green-900'
    if (score >= 60) return 'bg-yellow-100 dark:bg-yellow-900'
    return 'bg-red-100 dark:bg-red-900'
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        SEO Analysis
      </h3>

      <div className="space-y-4">
        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Page Title
          </h4>
          <p className="text-sm text-gray-900 dark:text-white bg-gray-50 dark:bg-gray-700 p-2 rounded">
            {data.title || 'No title found'}
          </p>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Meta Description
          </h4>
          <p className="text-sm text-gray-900 dark:text-white bg-gray-50 dark:bg-gray-700 p-2 rounded">
            {data.description || 'No description found'}
          </p>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            H1 Tag
          </h4>
          <p className="text-sm text-gray-900 dark:text-white bg-gray-50 dark:bg-gray-700 p-2 rounded">
            {data.h1 || 'No H1 found'}
          </p>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Word Count
          </h4>
          <span className="inline-block px-3 py-1 bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200 text-sm rounded-full">
            {data.wordCount} words
          </span>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Readability Score
          </h4>
          <div className="flex items-center justify-between mb-2">
            <span className={`text-lg font-bold ${getReadabilityColor(data.readability)}`}>
              {data.readability.toFixed(1)}
            </span>
          </div>
          <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
            <div
              className={`h-2 rounded-full ${getReadabilityBgColor(data.readability)}`}
              style={{ width: `${Math.min(data.readability, 100)}%` }}
            />
          </div>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Canonical URL
          </h4>
          <span className={`inline-block px-3 py-1 text-sm rounded-full ${data.canonical
              ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
              : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200'
            }`}>
            {data.canonical ? 'Present' : 'Missing'}
          </span>
        </div>
      </div>
    </div>
  )
}
