interface SeoData {
  score: number
  issues: string[]
  recommendations: string[]
}

interface SeoMetricsProps {
  data: SeoData
}

export function SeoMetrics({ data }: SeoMetricsProps) {
  const getScoreColor = (score: number) => {
    if (score >= 90) return 'text-green-600 dark:text-green-400'
    if (score >= 70) return 'text-yellow-600 dark:text-yellow-400'
    return 'text-red-600 dark:text-red-400'
  }

  const getScoreBgColor = (score: number) => {
    if (score >= 90) return 'bg-green-100 dark:bg-green-900'
    if (score >= 70) return 'bg-yellow-100 dark:bg-yellow-900'
    return 'bg-red-100 dark:bg-red-900'
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        SEO Analysis
      </h3>

      <div className="mb-6">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
            SEO Score
          </span>
          <span className={`text-2xl font-bold ${getScoreColor(data.score)}`}>
            {data.score}
          </span>
        </div>
        <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
          <div
            className={`h-2 rounded-full ${getScoreBgColor(data.score)}`}
            style={{ width: `${data.score}%` }}
          />
        </div>
      </div>

      <div className="space-y-4">
        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Issues Found ({data.issues.length})
          </h4>
          <div className="space-y-1">
            {data.issues.slice(0, 3).map((issue, index) => (
              <div
                key={index}
                className="text-sm text-red-600 dark:text-red-400 flex items-start"
              >
                <span className="mr-2">•</span>
                <span>{issue}</span>
              </div>
            ))}
            {data.issues.length > 3 && (
              <div className="text-sm text-gray-500 dark:text-gray-400">
                +{data.issues.length - 3} more issues
              </div>
            )}
          </div>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Recommendations
          </h4>
          <div className="space-y-1">
            {data.recommendations.slice(0, 3).map((rec, index) => (
              <div
                key={index}
                className="text-sm text-green-600 dark:text-green-400 flex items-start"
              >
                <span className="mr-2">•</span>
                <span>{rec}</span>
              </div>
            ))}
            {data.recommendations.length > 3 && (
              <div className="text-sm text-gray-500 dark:text-gray-400">
                +{data.recommendations.length - 3} more recommendations
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
