# TSP Bangladesh – Real‑World Routing Algorithm

A comprehensive implementation of the Travelling Salesman Problem (TSP) for Bangladesh's 64 districts. This project blends traditional heuristics with modern data-driven enhancements to generate practical, near-optimal travel routes.

## 🚀 Key Capabilities
- **Real Road Distance Integration** using the Google Maps Distance Matrix API.
- **Fallback Haversine Calculation** for routes not available via the API.
- **Caching Layer** to reduce redundant requests and improve performance.
- **Mandatory Route Sequences** allowing predefined district ordering.
- **2 Opt Algorithm**: Iterative Heuristic approach  with geographic awareness.
- **Clean Java Implementation** suitable for research, education, and prototyping.

## 🗺️ Core Concept
The goal is to compute an efficient travel path covering all districts in Bangladesh, beginning from Dhaka. The algorithm dynamically blends:
- API-based road distances (when available)
- Coordinate-based approximation (when needed)

This hybrid approach ensures operational resilience even when API data is incomplete.

## 📂 Project Structure
```
TSPBangladeshImproved/
├── src/
│   └── TSPBangladeshImproved.java
└── README.md
```

## 🔧 How It Works
1. Build an in-memory weighted graph of all district-to-district connections.
2. Query real road distances via API and persist results in a local cache.
3. Apply sequence constraints where required.
4. Run improved 2 opt Algorithm to compute a full tour.
5. Output the path, total distance, and computation summary.

## 🏁 Getting Started
### Prerequisites
- Java 17+

## 📈 Roadmap
- Visualization with Bangladesh administrative map
- Web-based dashboard for route exploration
- 2 opt Algorithm for the best output
- Alternative distance providers

## 📝 License
MIT License

## 🤝 Contributions
Pull requests are welcome. For major changes, kindly open an issue first.
