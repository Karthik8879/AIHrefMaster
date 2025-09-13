import { Shield, CheckCircle, XCircle, AlertTriangle } from 'lucide-react'

interface ThreatData {
  isSafe: boolean
  threats: string[]
}

interface SecurityStatusProps {
  data: ThreatData | null
}

export function SecurityStatus({ data }: SecurityStatusProps) {
  if (!data) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Security Status
        </h3>
        <div className="text-center text-gray-500 dark:text-gray-400">
          <p>Security data not available</p>
        </div>
      </div>
    )
  }
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        Security Status
      </h3>

      <div className="mb-6">
        <div className="flex items-center space-x-3">
          {data.isSafe ? (
            <CheckCircle className="h-8 w-8 text-green-600 dark:text-green-400" />
          ) : (
            <XCircle className="h-8 w-8 text-red-600 dark:text-red-400" />
          )}
          <div>
            <p className={`text-lg font-medium ${data.isSafe
              ? 'text-green-600 dark:text-green-400'
              : 'text-red-600 dark:text-red-400'
              }`}>
              {data.isSafe ? 'Safe to Visit' : 'Security Issues Detected'}
            </p>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {data.isSafe
                ? 'No threats detected'
                : `${data.threats.length} threat(s) found`
              }
            </p>
          </div>
        </div>
      </div>

      {data.threats.length > 0 && (
        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2 flex items-center">
            <AlertTriangle className="h-4 w-4 mr-2 text-yellow-500" />
            Threats Detected
          </h4>
          <div className="space-y-1">
            {data.threats.map((threat, index) => (
              <div
                key={index}
                className="text-sm text-red-600 dark:text-red-400 flex items-start"
              >
                <span className="mr-2">•</span>
                <span>{threat}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="mt-6 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
        <div className="flex items-start space-x-2">
          <Shield className="h-5 w-5 text-blue-600 dark:text-blue-400 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-blue-900 dark:text-blue-100">
              Security Check
            </p>
            <p className="text-xs text-blue-700 dark:text-blue-300 mt-1">
              This analysis is based on Google Safe Browsing data and other security sources.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
