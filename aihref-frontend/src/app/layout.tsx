import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'AIHref - Website Analytics Platform',
  description: 'Get comprehensive insights about website traffic, performance, SEO, and technology stack - completely free.',
  keywords: 'website analytics, traffic analysis, SEO tools, performance monitoring, web vitals',
  authors: [{ name: 'AIHref Team' }],
  openGraph: {
    title: 'AIHref - Website Analytics Platform',
    description: 'Get comprehensive insights about website traffic, performance, SEO, and technology stack - completely free.',
    type: 'website',
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.className} antialiased`}>
        {children}
      </body>
    </html>
  );
}