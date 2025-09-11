# AIHref - Zero-Cost SimilarWeb Clone

A comprehensive website analytics platform that provides traffic insights, performance metrics, SEO analysis, and technology stack information - completely free to use.

![AIHref Logo](https://img.shields.io/badge/AIHref-Analytics%20Platform-blue?style=for-the-badge&logo=chart-bar)

## 🚀 Live Demo

- **Frontend**: [https://aihref.vercel.app](https://aihref.vercel.app)
- **Backend API**: [https://aihref-backend.onrender.com](https://aihref-backend.onrender.com)
- **API Documentation**: [https://aihref-backend.onrender.com/swagger-ui.html](https://aihref-backend.onrender.com/swagger-ui.html)

## 📊 Features

### Core Analytics
- **Traffic Analysis**: Estimated monthly visits, global rank, bounce rate, session duration
- **Geographic Data**: Traffic distribution by country with detailed breakdowns
- **Device Analytics**: Desktop vs mobile traffic distribution
- **Referrer Analysis**: Top traffic sources and social media shares

### Performance Metrics
- **Core Web Vitals**: LCP, FID, CLS measurements
- **PageSpeed Insights**: Performance scores and recommendations
- **Screenshot Capture**: Visual representation of analyzed pages

### SEO & Technology
- **SEO Analysis**: Title optimization, meta descriptions, readability scores
- **Technology Stack**: HTTP/2, IPv6, SSL certificate analysis
- **Security Assessment**: Security headers and threat detection

### Real-time Data
- **Live Traffic Index**: Current traffic levels from Cloudflare Radar
- **Trend Analysis**: 7-day traffic patterns and historical data

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Next.js 14    │    │  Spring Boot 3  │    │   MongoDB       │
│   Frontend      │◄──►│   WebFlux API   │◄──►│   Database      │
│   (TypeScript)  │    │   (Java 21)     │    │   (Atlas M0)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         │              ┌─────────────────┐              │
         │              │     Redis       │              │
         │              │   (Rate Limit)  │              │
         │              └─────────────────┘              │
         │                       │                       │
         │              ┌─────────────────┐              │
         │              │  External APIs  │              │
         │              │                 │              │
         │              │ • SimilarWeb    │              │
         │              │ • PageSpeed     │              │
         │              │ • RDAP          │              │
         │              │ • Cloudflare    │              │
         │              │ • Safe Browsing │              │
         │              └─────────────────┘              │
```

## 🛠️ Tech Stack

### Backend
- **Spring Boot 3.2.2** with WebFlux (reactive programming)
- **Java 21** with modern language features
- **Spring Data MongoDB** (reactive) for data persistence
- **Spring Cache** with Caffeine for performance
- **Redis** for rate limiting and caching
- **Lombok** for reducing boilerplate code
- **MapStruct** for object mapping
- **Swagger OpenAPI** for API documentation

### Frontend
- **Next.js 14** with App Router
- **TypeScript 5** for type safety
- **TailwindCSS 3.4** for styling
- **Recharts 2.5** for data visualization
- **Framer Motion** for animations
- **SWR** for data fetching
- **Zustand** for state management
- **next-themes** for dark/light mode

### Infrastructure
- **MongoDB Atlas M0** (free tier) for database
- **Redis Upstash** (free tier) for caching
- **Docker** for containerization
- **GitHub Actions** for CI/CD
- **Render** (free tier) for backend hosting
- **Vercel** (free tier) for frontend hosting

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- Docker & Docker Compose
- MongoDB Atlas account
- Redis Upstash account

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/aihref.git
   cd aihref
   ```

2. **Set up environment variables**
   ```bash
   # Backend (.env)
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/aihref
   REDIS_URL=redis://username:password@host:port
   LIGHTHOUSE_API_KEY=your_google_api_key
   SAFE_BROWSING_API_KEY=your_safe_browsing_key

   # Frontend (.env.local)
   NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
   ```

3. **Start with Docker Compose**
   ```bash
   docker-compose up -d
   ```

4. **Or run locally**
   ```bash
   # Backend
   cd aihref-backend
   ./mvnw spring-boot:run

   # Frontend
   cd aihref-frontend
   npm install
   npm run dev
   ```

5. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Docs: http://localhost:8080/swagger-ui.html

## 📡 API Endpoints

### Analysis
- `POST /api/v1/analyse` - Start website analysis
- `GET /api/v1/lookup/{id}` - Get analysis results
- `GET /api/v1/export/{id}?type=csv|json` - Export data

### Health
- `GET /api/v1/health` - Health check

### Rate Limiting
- 120 requests per minute per IP address
- Redis-based rate limiting with sliding window

## 🗄️ Database Schema

```javascript
{
  _id: ObjectId,
  url: String (unique),
  requestedAt: Instant,
  expiresAt: Instant,
  traffic: {
    visits: Long,
    rank: Long,
    bounce: Double,
    avgDuration: Long,
    countries: [{ country: String, share: Double, visits: Long }],
    devices: [{ device: String, share: Double }],
    referrers: [{ site: String, share: Double }],
    searchShare: Double,
    socialShare: Double
  },
  webVitals: {
    lcp: Double,
    fid: Double,
    cls: Double,
    score: Int,
    screenshotB64: String
  },
  seo: {
    title: String,
    description: String,
    h1: String,
    wordCount: Int,
    readability: Double,
    canonical: Boolean
  },
  tech: {
    ipv6: Boolean,
    http2: Boolean,
    sslDaysLeft: Int,
    securityGrade: String
  },
  live: {
    trafficIndex: Int,
    lastUpdated: Instant
  },
  threat: {
    safeBrowsing: Boolean
  }
}
```

## 🔧 Configuration

### Backend Configuration
```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}
    redis:
      url: ${REDIS_URL}
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=24h

external:
  apis:
    similarweb:
      base-url: https://api.similarweb.com/v4
    pagespeed:
      base-url: https://www.googleapis.com/pagespeedonline/v5
    rdap:
      base-url: https://rdap.org
    cloudflare:
      base-url: https://api.cloudflare.com/client/v4/radar
```

### Frontend Configuration
```typescript
// next.config.js
const nextConfig = {
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL
  }
}
```

## 🧪 Testing

### Backend Tests
```bash
cd aihref-backend
./mvnw test
```

### Frontend Tests
```bash
cd aihref-frontend
npm test
```

### Integration Tests
```bash
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## 📦 Deployment

### Docker Deployment
```bash
# Build and run
docker-compose up -d

# Scale services
docker-compose up -d --scale backend=3
```

### Production Deployment
1. **Backend**: Deploy to Render using `render.yaml`
2. **Frontend**: Deploy to Vercel
3. **Database**: MongoDB Atlas M0
4. **Cache**: Redis Upstash

## 🔒 Security Features

- **Rate Limiting**: 120 requests/minute per IP
- **Input Validation**: URL format validation
- **CORS Configuration**: Secure cross-origin requests
- **Error Handling**: Comprehensive error responses
- **Health Checks**: Application and dependency monitoring

## 📈 Performance Optimizations

- **Reactive Programming**: Non-blocking I/O with WebFlux
- **Caching**: 24-hour TTL with Caffeine
- **Connection Pooling**: Optimized database connections
- **CDN**: Static asset delivery via Vercel
- **Image Optimization**: Next.js automatic optimization

## 🎯 Success Metrics

- ✅ Page load time < 2 seconds
- ✅ All 15 metrics visible on dashboard
- ✅ Export functionality (CSV/JSON/PDF)
- ✅ Lighthouse score ≥ 95
- ✅ CI/CD pipeline with green status

## 💰 Cost Analysis

| Service | Tier | Monthly Cost |
|---------|------|--------------|
| MongoDB Atlas | M0 | $0 |
| Redis Upstash | Free | $0 |
| Render | Free | $0 |
| Vercel | Hobby | $0 |
| **Total** | | **$0** |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **SimilarWeb** for traffic data API
- **Google PageSpeed Insights** for performance metrics
- **Cloudflare Radar** for live traffic data
- **Spring Boot** and **Next.js** communities
- **Open source** contributors

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/aihref/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/aihref/discussions)
- **Email**: support@aihref.com

---

**Built with ❤️ for the developer community**

*AIHref - Making website analytics accessible to everyone*
