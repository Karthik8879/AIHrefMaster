interface TechData {
  ipv6: boolean
  http2: boolean
  sslDaysLeft: number
  securityGrade: string
}

interface TechnologyStackProps {
  data: TechData | null
}

export function TechnologyStack({ data }: TechnologyStackProps) {
  if (!data) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Technology Stack
        </h3>
        <div className="text-center text-gray-500 dark:text-gray-400">
          <p>Technology data not available</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        Technology Stack
      </h3>

      <div className="space-y-4">
        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            IPv6 Support
          </h4>
          <span className={`inline-block px-3 py-1 text-sm rounded-full ${data.ipv6
              ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
              : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200'
            }`}>
            {data.ipv6 ? 'Supported' : 'Not Supported'}
          </span>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            HTTP/2 Support
          </h4>
          <span className={`inline-block px-3 py-1 text-sm rounded-full ${data.http2
              ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
              : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200'
            }`}>
            {data.http2 ? 'Supported' : 'Not Supported'}
          </span>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            SSL Certificate
          </h4>
          <span className={`inline-block px-3 py-1 text-sm rounded-full ${data.sslDaysLeft > 30
              ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
              : data.sslDaysLeft > 0
                ? 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200'
                : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200'
            }`}>
            {data.sslDaysLeft > 0 ? `${data.sslDaysLeft} days left` : 'Expired'}
          </span>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Security Grade
          </h4>
          <span className={`inline-block px-3 py-1 text-sm rounded-full ${data.securityGrade === 'A'
              ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
              : data.securityGrade === 'B'
                ? 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200'
                : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200'
            }`}>
            Grade {data.securityGrade}
          </span>
        </div>
      </div>
    </div>
  )
}
