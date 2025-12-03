import java.util.*;
import java.io.*;
import java.net.*;
import java.net.http.*;

/**
 * TSP Bangladesh with Real Road Distances
 */
public class TSPBangladeshImproved {

    private Map<String, Map<String, Double>> graph;
    private Map<String, DistrictCoords> coordinates;
    private List<String> allDistricts;
    private final String startDistrict = "Dhaka";
    private final Random rng;
    private Map<String, Double> distanceCache;

    // Store district coordinates for Haversine fallback
    static class DistrictCoords {
        double lat, lon;
        DistrictCoords(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public TSPBangladeshImproved(long seed) {
        graph = new HashMap<>();
        coordinates = new HashMap<>();
        allDistricts = new ArrayList<>();
        distanceCache = new HashMap<>();
        rng = new Random(seed);
        initializeDistricts();
        initializeCoordinates();
        initializeEdges();
    }

    private void initializeDistricts() {
        String[] districts = {
            "Dhaka", "Faridpur", "Gazipur", "Gopalganj", "Jamalpur", "Kishoreganj",
            "Madaripur", "Manikganj", "Munshiganj", "Mymensingh", "Narayanganj",
            "Narsingdi", "Netrokona", "Rajbari", "Shariatpur", "Sherpur", "Tangail",
            "Bogura", "Joypurhat", "Naogaon", "Natore", "Chapainawabganj", "Pabna",
            "Rajshahi", "Sirajganj", "Dinajpur", "Gaibandha", "Kurigram", "Lalmonirhat",
            "Nilphamari", "Panchagarh", "Rangpur", "Thakurgaon", "Habiganj", "Moulvibazar",
            "Sunamganj", "Sylhet", "Bagerhat", "Chuadanga", "Jessore", "Jhenaidah",
            "Khulna", "Kushtia", "Magura", "Meherpur", "Narail", "Satkhira",
            "Bandarban", "Brahmanbaria", "Chandpur", "Chittagong", "Comilla", "Cox's Bazar",
            "Feni", "Khagrachhari", "Lakshmipur", "Noakhali", "Rangamati", "Barisal",
            "Barguna", "Bhola", "Jhalokati", "Patuakhali", "Pirojpur"
        };
        for (String d : districts) {
            allDistricts.add(d);
            graph.put(d, new HashMap<>());
        }
    }

    // Approximate coordinates for Bangladesh districts
    private void initializeCoordinates() {
        coordinates.put("Dhaka", new DistrictCoords(23.8103, 90.4125));
        coordinates.put("Faridpur", new DistrictCoords(23.6070, 89.8429));
        coordinates.put("Gazipur", new DistrictCoords(24.0022, 90.4264));
        coordinates.put("Gopalganj", new DistrictCoords(23.0050, 89.8266));
        coordinates.put("Jamalpur", new DistrictCoords(24.9375, 89.9480));
        coordinates.put("Kishoreganj", new DistrictCoords(24.4260, 90.7760));
        coordinates.put("Madaripur", new DistrictCoords(23.1641, 90.1897));
        coordinates.put("Manikganj", new DistrictCoords(23.8617, 90.0003));
        coordinates.put("Munshiganj", new DistrictCoords(23.5422, 90.5305));
        coordinates.put("Mymensingh", new DistrictCoords(24.7471, 90.4203));
        coordinates.put("Narayanganj", new DistrictCoords(23.6238, 90.5000));
        coordinates.put("Narsingdi", new DistrictCoords(23.9229, 90.7176));
        coordinates.put("Netrokona", new DistrictCoords(24.8800, 90.7270));
        coordinates.put("Rajbari", new DistrictCoords(23.7574, 89.6444));
        coordinates.put("Shariatpur", new DistrictCoords(23.2423, 90.4348));
        coordinates.put("Sherpur", new DistrictCoords(25.0204, 90.0152));
        coordinates.put("Tangail", new DistrictCoords(24.2513, 89.9167));
        
        coordinates.put("Bogura", new DistrictCoords(24.8465, 89.3770));
        coordinates.put("Joypurhat", new DistrictCoords(25.0968, 89.0227));
        coordinates.put("Naogaon", new DistrictCoords(24.7936, 88.9318));
        coordinates.put("Natore", new DistrictCoords(24.4206, 89.0000));
        coordinates.put("Chapainawabganj", new DistrictCoords(24.5965, 88.2775));
        coordinates.put("Pabna", new DistrictCoords(24.0064, 89.2372));
        coordinates.put("Rajshahi", new DistrictCoords(24.3745, 88.6042));
        coordinates.put("Sirajganj", new DistrictCoords(24.4533, 89.7006));
        
        coordinates.put("Dinajpur", new DistrictCoords(25.6217, 88.6354));
        coordinates.put("Gaibandha", new DistrictCoords(25.3288, 89.5430));
        coordinates.put("Kurigram", new DistrictCoords(25.8072, 89.6361));
        coordinates.put("Lalmonirhat", new DistrictCoords(25.9923, 89.2847));
        coordinates.put("Nilphamari", new DistrictCoords(25.9317, 88.8560));
        coordinates.put("Panchagarh", new DistrictCoords(26.3411, 88.5541));
        coordinates.put("Rangpur", new DistrictCoords(25.7439, 89.2752));
        coordinates.put("Thakurgaon", new DistrictCoords(26.0336, 88.4616));
        
        coordinates.put("Habiganj", new DistrictCoords(24.3745, 91.4156));
        coordinates.put("Moulvibazar", new DistrictCoords(24.4821, 91.7318));
        coordinates.put("Sunamganj", new DistrictCoords(25.0658, 91.3950));
        coordinates.put("Sylhet", new DistrictCoords(24.8949, 91.8687));
        
        coordinates.put("Bagerhat", new DistrictCoords(22.6602, 89.7895));
        coordinates.put("Chuadanga", new DistrictCoords(23.6401, 88.8410));
        coordinates.put("Jessore", new DistrictCoords(23.1634, 89.2182));
        coordinates.put("Jhenaidah", new DistrictCoords(23.5448, 89.1539));
        coordinates.put("Khulna", new DistrictCoords(22.8456, 89.5403));
        coordinates.put("Kushtia", new DistrictCoords(23.9013, 89.1199));
        coordinates.put("Magura", new DistrictCoords(23.4855, 89.4198));
        coordinates.put("Meherpur", new DistrictCoords(23.7625, 88.6318));
        coordinates.put("Narail", new DistrictCoords(23.1163, 89.4840));
        coordinates.put("Satkhira", new DistrictCoords(22.7090, 89.0700));
        
        coordinates.put("Bandarban", new DistrictCoords(22.1953, 92.2183));
        coordinates.put("Brahmanbaria", new DistrictCoords(23.9571, 91.1115));
        coordinates.put("Chandpur", new DistrictCoords(23.2513, 90.8518));
        coordinates.put("Chittagong", new DistrictCoords(22.3569, 91.7832));
        coordinates.put("Comilla", new DistrictCoords(23.4607, 91.1809));
        coordinates.put("Cox's Bazar", new DistrictCoords(21.4272, 92.0058));
        coordinates.put("Feni", new DistrictCoords(23.0159, 91.3976));
        coordinates.put("Khagrachhari", new DistrictCoords(23.1193, 91.9847));
        coordinates.put("Lakshmipur", new DistrictCoords(22.9447, 90.8412));
        coordinates.put("Noakhali", new DistrictCoords(22.8696, 91.0997));
        coordinates.put("Rangamati", new DistrictCoords(22.7324, 92.2985));
        
        coordinates.put("Barisal", new DistrictCoords(22.7010, 90.3535));
        coordinates.put("Barguna", new DistrictCoords(22.1590, 90.1190));
        coordinates.put("Bhola", new DistrictCoords(22.6859, 90.6482));
        coordinates.put("Jhalokati", new DistrictCoords(22.6406, 90.1987));
        coordinates.put("Patuakhali", new DistrictCoords(22.3596, 90.3298));
        coordinates.put("Pirojpur", new DistrictCoords(22.5791, 89.9759));
    }

    private void addEdge(String a, String b, double distance) {
        if (!graph.containsKey(a) || !graph.containsKey(b)) return;
        graph.get(a).put(b, distance);
        graph.get(b).put(a, distance);
    }

    // Complete highway network with real distances
    private void initializeEdges() {
        // === Dhaka Division ===
        addEdge("Dhaka", "Gazipur", 23.1);
        addEdge("Dhaka", "Narayanganj", 25.4);
        addEdge("Dhaka", "Munshiganj", 43.6);
        addEdge("Dhaka", "Manikganj", 53.7);
        addEdge("Dhaka", "Tangail", 84.7);
        addEdge("Dhaka", "Faridpur", 122.0);

        addEdge("Faridpur", "Rajbari", 30.0);
        addEdge("Faridpur", "Madaripur", 72.5);
        addEdge("Faridpur", "Gopalganj", 95.3);
        addEdge("Faridpur", "Magura", 52.2);
        addEdge("Faridpur", "Manikganj", 58.7);
        addEdge("Faridpur", "Munshiganj", 103.0);
        addEdge("Faridpur", "Narail", 83.3);

        addEdge("Gazipur", "Tangail", 62.4);
        addEdge("Gazipur", "Mymensingh", 91.8);
        addEdge("Gazipur", "Narsingdi", 43.5);
        addEdge("Gazipur", "Kishoreganj", 80.5);
        addEdge("Gazipur", "Narayanganj", 51.2);

        addEdge("Gopalganj", "Madaripur", 60.8);
        addEdge("Gopalganj", "Khulna", 65.0);
        addEdge("Gopalganj", "Narail", 57.4);

        addEdge("Jamalpur", "Sherpur", 19.0);
        addEdge("Jamalpur", "Mymensingh", 59.4);
        addEdge("Jamalpur", "Gaibandha", 61.4);
        addEdge("Jamalpur", "Kurigram", 103.4);
        addEdge("Jamalpur", "Bogura", 58.8);
        addEdge("Jamalpur", "Sirajganj", 111.0);
        addEdge("Jamalpur", "Tangail", 89.3);
        
        addEdge("Kishoreganj", "Narsingdi", 68.4);
        addEdge("Kishoreganj", "Mymensingh", 67.8);
        addEdge("Kishoreganj", "Brahmanbaria", 73.0);
        addEdge("Kishoreganj", "Netrokona", 57.5);
        addEdge("Kishoreganj", "Habiganj", 64.26);
        addEdge("Kishoreganj", "Sunamganj", 162.0);

        addEdge("Madaripur", "Shariatpur", 19.0);
        addEdge("Madaripur", "Barisal", 58.3);
        addEdge("Madaripur", "Munshiganj", 85.3);

        addEdge("Manikganj", "Dhaka", 53.7);
        addEdge("Manikganj", "Rajbari", 56.1);
        addEdge("Manikganj", "Faridpur", 58.7);
        addEdge("Manikganj", "Tangail", 67.3);
        addEdge("Manikganj", "Pabna", 94.3);
        addEdge("Manikganj", "Sirajganj", 110.0);

        addEdge("Munshiganj", "Narayanganj", 15.7);
        addEdge("Munshiganj", "Shariatpur", 70.6);

        addEdge("Mymensingh", "Sherpur", 68.1);
        addEdge("Mymensingh", "Netrokona", 37.6);
        addEdge("Mymensingh", "Tangail", 97.2);

        addEdge("Narayanganj", "Comilla", 96.7);
        addEdge("Narayanganj", "Narsingdi", 50.1);

        addEdge("Narsingdi", "Brahmanbaria", 54.1);
        addEdge("Narsingdi", "Narayanganj", 46.3);

        addEdge("Netrokona", "Sunamganj", 95.1);
        addEdge("Netrokona", "Mymensingh", 37.6);

        addEdge("Rajbari", "Manikganj", 55.8);
        addEdge("Rajbari", "Magura", 47.9);
        addEdge("Rajbari", "Kushtia", 65.7);

        addEdge("Tangail", "Sirajganj", 43.6);
        addEdge("Tangail", "Mymensingh", 100.0);

        // === Rajshahi Division ===
        addEdge("Bogura", "Joypurhat", 56.4);
        addEdge("Bogura", "Natore", 71.3);
        addEdge("Bogura", "Gaibandha", 70.4);

        addEdge("Joypurhat", "Naogaon", 43.8);
        addEdge("Joypurhat", "Dinajpur", 86.8);

        addEdge("Naogaon", "Natore", 53.3);
        addEdge("Naogaon", "Rajshahi", 79.6);
        addEdge("Naogaon", "Chapainawabganj", 94.1);

        addEdge("Natore", "Pabna", 61.3);
        addEdge("Natore", "Bogura", 70.9);
        addEdge("Natore", "Rajshahi", 42.4);
        addEdge("Natore", "Naogaon", 53.3);

        addEdge("Chapainawabganj", "Rajshahi", 48.9);
        addEdge("Chapainawabganj", "Naogaon", 94.2);

        addEdge("Pabna", "Sirajganj", 91.5);
        addEdge("Pabna", "Natore", 61.9);
        addEdge("Pabna", "Kushtia", 30.2);

        addEdge("Rajshahi", "Natore", 42.4);
        addEdge("Rajshahi", "Chapainawabganj", 48.9);
        addEdge("Rajshahi", "Naogaon", 79.6);
        addEdge("Rajshahi", "Kushtia", 105.0);

        addEdge("Sirajganj", "Pabna", 93.4);
        addEdge("Sirajganj", "Bogura", 71.8);
        addEdge("Sirajganj", "Tangail", 45.1);
        addEdge("Sirajganj", "Natore", 90.6);

        // === Rangpur Division ===
        addEdge("Dinajpur", "Thakurgaon", 56.8);
        addEdge("Dinajpur", "Rangpur", 71.4);
        addEdge("Dinajpur", "Nilphamari", 52.0);

        addEdge("Gaibandha", "Rangpur", 61.1);
        addEdge("Gaibandha", "Kurigram", 63.3);
        addEdge("Gaibandha", "Bogura", 72.0);

        addEdge("Kurigram", "Lalmonirhat", 29.8);
        addEdge("Kurigram", "Rangpur", 44.3);
        addEdge("Kurigram", "Gaibandha", 64.1);

        addEdge("Lalmonirhat", "Kurigram", 29.6);
        addEdge("Lalmonirhat", "Nilphamari", 80.1);
        addEdge("Lalmonirhat", "Rangpur", 38.2);

        addEdge("Nilphamari", "Rangpur", 55.7);
        addEdge("Nilphamari", "Lalmonirhat", 80.1);
        addEdge("Nilphamari", "Panchagarh", 66.5);

        addEdge("Panchagarh", "Thakurgaon", 38.8);
        addEdge("Panchagarh", "Nilphamari", 66.4);
        addEdge("Panchagarh", "Dinajpur", 97.1);

        addEdge("Rangpur", "Lalmonirhat", 38.2);
        addEdge("Rangpur", "Nilphamari", 55.9);
        addEdge("Rangpur", "Kurigram", 44.1);
        addEdge("Rangpur", "Gaibandha", 74.8);

        addEdge("Thakurgaon", "Panchagarh", 38.9);
        addEdge("Thakurgaon", "Dinajpur", 56.8);

        // === Sylhet Division ===
        addEdge("Habiganj", "Moulvibazar", 63.2);
        addEdge("Habiganj", "Sylhet", 81.4);
        addEdge("Habiganj", "Sunamganj", 95.0);
        addEdge("Habiganj", "Brahmanbaria", 60.2);

        addEdge("Moulvibazar", "Habiganj", 63.3);
        addEdge("Moulvibazar", "Sylhet", 59.9);

        addEdge("Sunamganj", "Sylhet", 65.6);
        addEdge("Sunamganj", "Habiganj", 95.2);
        addEdge("Sunamganj", "Netrokona", 95.1);

        addEdge("Sylhet", "Moulvibazar", 60.1);
        addEdge("Sylhet", "Sunamganj", 62.6);
        addEdge("Sylhet", "Habiganj", 81.5);

        // === Khulna Division ===
        addEdge("Bagerhat", "Khulna", 63.4);
        addEdge("Bagerhat", "Pirojpur", 26.1);
        addEdge("Bagerhat", "Gopalganj", 49.9);

        addEdge("Chuadanga", "Meherpur", 30.6);
        addEdge("Chuadanga", "Kushtia", 50.0);
        addEdge("Chuadanga", "Jhenaidah", 36.4);

        addEdge("Jessore", "Jhenaidah", 52.2);
        addEdge("Jessore", "Khulna", 59.8);
        addEdge("Jessore", "Narail", 33.0);
        addEdge("Jessore", "Magura", 44.7);
        addEdge("Jessore", "Chuadanga", 74.5);
        addEdge("Jessore", "Satkhira", 63.8);

        addEdge("Jhenaidah", "Kushtia", 46.8);
        addEdge("Jhenaidah", "Jessore", 45.7);
        addEdge("Jhenaidah", "Magura", 27.5);
        addEdge("Jhenaidah", "Chuadanga", 36.4);

        addEdge("Khulna", "Jessore", 59.9);
        addEdge("Khulna", "Narail", 49.8);
        addEdge("Khulna", "Bagerhat", 36.4);
        addEdge("Khulna", "Satkhira", 58.2);

        addEdge("Kushtia", "Meherpur", 57.9);
        addEdge("Kushtia", "Jhenaidah", 45.9);
        addEdge("Kushtia", "Chuadanga", 49.8);
        addEdge("Kushtia", "Pabna", 29.6);
        addEdge("Kushtia", "Rajbari", 65.1);
        addEdge("Kushtia", "Natore", 78.4);

        addEdge("Magura", "Jhenaidah", 27.5);
        addEdge("Magura", "Faridpur", 52.1);
        addEdge("Magura", "Narail", 44.6);
        addEdge("Magura", "Jessore", 44.7);
        addEdge("Magura", "Rajbari", 49.5);

        addEdge("Meherpur", "Chuadanga", 29.7);
        addEdge("Meherpur", "Kushtia", 58.4);

        addEdge("Narail", "Jessore", 33.0);
        addEdge("Narail", "Khulna", 49.8);
        addEdge("Narail", "Magura", 47.8);
        addEdge("Narail", "Gopalganj", 57.4);

        addEdge("Satkhira", "Khulna", 57.4);
        addEdge("Satkhira", "Jessore", 63.0);

        // === Chittagong Division ===
        addEdge("Bandarban", "Rangamati", 72.6);
        addEdge("Bandarban", "Chittagong", 74.8);
        addEdge("Bandarban", "Cox's Bazar", 116);

        addEdge("Brahmanbaria", "Comilla", 67.6);
        addEdge("Brahmanbaria", "Habiganj", 60.3);
        addEdge("Brahmanbaria", "Narsingdi", 54.1);

        addEdge("Chandpur", "Comilla", 69.8);
        addEdge("Chandpur", "Lakshmipur", 43.9);
        addEdge("Chandpur", "Narayanganj", 95.1);

        addEdge("Chittagong", "Bandarban", 74.0);
        addEdge("Chittagong", "Feni", 100.0);
        addEdge("Chittagong", "Cox's Bazar", 145.0);
        addEdge("Chittagong", "Rangamati", 63.0);
        addEdge("Chittagong", "Khagrachhari", 105);

        addEdge("Comilla", "Brahmanbaria", 67.7);
        addEdge("Comilla", "Feni", 58.3);
        addEdge("Comilla", "Noakhali", 70.1);
        addEdge("Comilla", "Chandpur", 70.3);

        addEdge("Cox's Bazar", "Chittagong", 138.0);
        addEdge("Cox's Bazar", "Bandarban", 116.0);

        addEdge("Feni", "Chittagong", 91.3);
        addEdge("Feni", "Comilla", 58.1);
        addEdge("Feni", "Noakhali", 45.0);

        addEdge("Khagrachhari", "Rangamati", 68.1);
        addEdge("Khagrachhari", "Chittagong", 105.0);

        addEdge("Lakshmipur", "Noakhali", 37.9);
        addEdge("Lakshmipur", "Chandpur", 43.8);

        addEdge("Noakhali", "Feni", 45.6);
        addEdge("Noakhali", "Lakshmipur", 34.4);
        addEdge("Noakhali", "Comilla", 69.6);

        addEdge("Rangamati", "Khagrachhari", 68.1);
        addEdge("Rangamati", "Bandarban", 72.6);
        addEdge("Rangamati", "Chittagong", 61.5);

        // === Barisal Division ===
        addEdge("Barisal", "Patuakhali", 44.7);
        addEdge("Barisal", "Jhalokati", 21.2);
        addEdge("Barisal", "Bhola", 48.1);
        addEdge("Barisal", "Pirojpur", 54.1);

        addEdge("Barguna", "Patuakhali", 44.5);
        addEdge("Barguna", "Pirojpur", 72.3);
        addEdge("Barguna", "Jhalokati", 89.7);

        addEdge("Bhola", "Barisal", 48.2);
        addEdge("Bhola", "Patuakhali", 76.9);

        addEdge("Jhalokati", "Barisal", 21.2);
        addEdge("Jhalokati", "Pirojpur", 31.9);
        addEdge("Jhalokati", "Barguna", 76.2);

        addEdge("Patuakhali", "Pirojpur", 88.4);
        addEdge("Patuakhali", "Barguna", 40.7);
        addEdge("Patuakhali", "Barisal", 44.7);
        addEdge("Patuakhali", "Bhola", 77.0);

        addEdge("Pirojpur", "Jhalokati", 31.9);
        addEdge("Pirojpur", "Gopalganj", 57.3);
        addEdge("Pirojpur", "Bagerhat", 25.1);
        addEdge("Pirojpur", "Barisal", 52.2);
        addEdge("Pirojpur", "Barguna", 68.3);
    }

    /**
     * Calculate Haversine distance between two districts
     * This gives straight-line distance, multiply by 1.3 for road estimate
     */
    private double calculateHaversineDistance(String a, String b) {
        DistrictCoords c1 = coordinates.get(a);
        DistrictCoords c2 = coordinates.get(b);
        if (c1 == null || c2 == null) return 300.0; // fallback
        
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(c2.lat - c1.lat);
        double dLon = Math.toRadians(c2.lon - c1.lon);
        double lat1 = Math.toRadians(c1.lat);
        double lat2 = Math.toRadians(c2.lat);
        
        double a_calc = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a_calc), Math.sqrt(1-a_calc));
        
        // Multiply by 1.3 to approximate road distance (roads aren't straight)
        return R * c * 1.3;
    }

    /**
     * Get distance between two districts
     * Uses: 1) Existing edge, 2) Cached distance, 3) Haversine estimate
     */
    public double getDistance(String a, String b) {
        // Check if direct edge exists
        if (graph.get(a).containsKey(b)) {
            return graph.get(a).get(b);
        }
        
        // Check cache
        String key = a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
        if (distanceCache.containsKey(key)) {
            return distanceCache.get(key);
        }
        
        // Calculate Haversine distance as estimate
        double dist = calculateHaversineDistance(a, b);
        distanceCache.put(key, dist);
        return dist;
    }

    /**
     * Improved Nearest Neighbor with geographic awareness
     */
    public List<String> nearestNeighborFrom(String start) {
        Set<String> visited = new HashSet<>();
        List<String> tour = new ArrayList<>();
        String current = start;
        tour.add(start);
        visited.add(current);

        while (visited.size() < allDistricts.size()) {
            String best = null;
            double bestDist = Double.POSITIVE_INFINITY;
            
            for (String candidate : allDistricts) {
                if (!visited.contains(candidate)) {
                    double dist = getDistance(current, candidate);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = candidate;
                    }
                }
            }
            
            if (best == null) break;
            tour.add(best);
            visited.add(best);
            current = best;
        }
        
        // Close the tour
        tour.add(start);
        return tour;
    }

    /**
     * Calculate total tour distance
     */
    public double calculateTourDistance(List<String> tour) {
        if (tour == null || tour.size() < 2) return Double.POSITIVE_INFINITY;
        double total = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            total += getDistance(tour.get(i), tour.get(i + 1));
        }
        return total;
    }

    /**
     * 2-Opt improvement
     */
    public List<String> twoOptImprove(List<String> tour) {
        if (tour == null || tour.size() < 4) return tour;
        List<String> best = new ArrayList<>(tour);
        boolean improved = true;
        
        while (improved) {
            improved = false;
            int n = best.size();
            
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    String A = best.get(i - 1);
                    String B = best.get(i);
                    String C = best.get(j);
                    String D = best.get(j + 1);
                    
                    double currentDist = getDistance(A, B) + getDistance(C, D);
                    double newDist = getDistance(A, C) + getDistance(B, D);
                    
                    if (newDist < currentDist - 0.001) {
                        reverseSubList(best, i, j);
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    private void reverseSubList(List<String> list, int i, int j) {
        while (i < j) {
            Collections.swap(list, i, j);
            i++; j--;
        }
    }

    /**
     * Multi-start search with random initial tours
     */
    public Result multiStartSearch(int restarts) {
        List<String> bestTour = null;
        double bestDist = Double.POSITIVE_INFINITY;

        System.out.println("Progress: ");
        for (int r = 0; r < restarts; r++) {
            if (r % 20 == 0) System.out.print(".");
            
            // Try different starting points
            String startPoint = allDistricts.get(rng.nextInt(allDistricts.size()));
            List<String> tour = nearestNeighborFrom(startPoint);
            
            // Rotate tour to start from Dhaka
            tour = rotateTourToStart(tour, startDistrict);
            
            // Apply 2-opt
            tour = twoOptImprove(tour);
            
            double dist = calculateTourDistance(tour);
            if (dist < bestDist) {
                bestDist = dist;
                bestTour = new ArrayList<>(tour);
            }
        }
        System.out.println(" Done!");

        return new Result(bestTour, bestDist);
    }

    /**
     * Rotate tour to start from specified district
     */
    private List<String> rotateTourToStart(List<String> tour, String start) {
        if (tour == null || tour.isEmpty()) return tour;
        
        int idx = tour.indexOf(start);
        if (idx == -1 || idx == 0) return tour;
        
        List<String> rotated = new ArrayList<>();
        for (int i = idx; i < tour.size() - 1; i++) {
            rotated.add(tour.get(i));
        }
        for (int i = 0; i < idx; i++) {
            rotated.add(tour.get(i));
        }
        rotated.add(start); // close tour
        return rotated;
    }

    private void printTour(List<String> tour) {
        if (tour == null) {
            System.out.println("No valid tour found.");
            return;
        }
        System.out.println("\n=== Best Tour (Dhaka -> All 64 Districts -> Dhaka) ===");
        double total = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            String a = tour.get(i), b = tour.get(i + 1);
            double d = getDistance(a, b);
            System.out.printf("%2d. %-18s -> %-18s : %7.1f km\n", i + 1, a, b, d);
            total += d;
        }
        System.out.printf("\nTotal distance: %.1f km\n", total);
        System.out.println("Districts visited: " + (tour.size() - 1));
    }

    static class Result {
        List<String> tour;
        double distance;
        Result(List<String> t, double d) { tour = t; distance = d; }
    }

    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        TSPBangladeshImproved tsp = new TSPBangladeshImproved(seed);

        System.out.println("=== TSP Bangladesh - Improved Solver ===");
        System.out.println("Total districts: " + tsp.allDistricts.size());
        System.out.println("Starting from: " + tsp.startDistrict);
        System.out.println("\nRunning optimization...\n");

        long t0 = System.currentTimeMillis();
        Result result = tsp.multiStartSearch(100);
        long t1 = System.currentTimeMillis();

        System.out.println("\nCompleted in " + (t1 - t0) + " ms");
        tsp.printTour(result.tour);
        
        System.out.println("\n=== NOTES ===");
        System.out.println("• Distances use real highway data where available");
        System.out.println("• Missing routes estimated using geographic distance × 1.3");
        System.out.println("• For production use, integrate Google Maps Distance Matrix API");
    }
}