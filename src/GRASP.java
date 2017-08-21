package grasp;

import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class GRASP {

    public static double euc_2d(double[] c1, double[] c2) {
        return Math.sqrt(Math.pow((c1[0] - c2[0]), 2.0) + Math.pow((c1[1] - c2[1]), 2.0));
    }

    public static double cost(int[] perm, double[][] cities) {
        double distance = 0;

        for (int i = 0, c1 = 0; i < perm.length; i++, c1++) {
            int c2 = perm[(i + 1) % perm.length];
            // (i == perm.length - 1) ? perm[0] : perm[i + 1];
            distance += euc_2d(cities[c1], cities[c2]);
        }

        return distance;
    }

    public static int[] stochastic_two_opt(int[] permutation) {
        int[] perm = permutation.clone();
        Random rand = new Random();
        int c1 = rand.nextInt(perm.length + 1);
        int c2 = rand.nextInt(perm.length + 1);

        int[] exclude = { 
            c1,
            ((c1 + 1) % perm.length),
            ((c1 == 0) ? (perm.length - 1) : (c1 - 1))
            //(perm.length - ((-c1) % perm.length) - 1)
        };

        while (Arrays.binarySearch(exclude, (c2 = rand.nextInt(perm.length + 1))) >= 0){}

        if (c2 < c1) {
            int aux = c2;
            c2 = c1;
            c1 = c2;
        }

        int[] perm_old = perm.clone();
        for (int i = c1, j = c2 - 1; i < c2; i++, j--) {
            perm[i] = perm_old[j];
        }

        return perm;
    }

    public static Best local_search(Best best, double[][] cities, int max_no_improv) {
        int count = 0;

        do {
            int[] vector = stochastic_two_opt(best.getVector());
            Best candidate = new Best(cost(vector, cities), vector);
            count = (candidate.getCost() < best.getCost() ? 0 : count + 1);

            if (candidate.getCost() < best.getCost()) {
                best = candidate;
            }
        } while (count < max_no_improv);

        return best;
    }

    public static Best construct_randomized_greedy_solution(double[][] cities, double alpha) {
        Random rand = new Random();
        int random_city_number = rand.nextInt(cities.length + 1);

        List<Integer> candidate_vector = new ArrayList<>();
        candidate_vector.add(random_city_number);

        List<Integer> allCities = new ArrayList<>();
        for (int i = 0; i < cities.length; i++) {
            allCities.add(i);
        }

        while (candidate_vector.size() < cities.length) {

            List<Integer> candidates = new ArrayList<>();
            candidates.addAll(allCities);
            candidates.removeAll(candidate_vector);

            List<Double> costs = new ArrayList<>();
            for (int i = 0; i < candidates.size(); i++) {
                costs.add(euc_2d(cities[candidate_vector.get(candidate_vector.size() - 1)], cities[i]));
            }

            List<Integer> rcl = new ArrayList<>();
            Double max = costs.stream()
                    .mapToDouble(v -> v)
                    .max()
                    .getAsDouble();
            Double min = costs.stream()
                    .mapToDouble(v -> v)
                    .min()
                    .getAsDouble();
            int i = 0;
            for (Double c : costs) {
                if (c <= (min + alpha * (max - min))) {
                    rcl.add(candidates.get(i));
                }
                i++;
            }

            //if(rcl.size() > 0){
            candidate_vector.add(rcl.get(rand.nextInt(rcl.size())));
            //}
        }

        int[] candidate_array = candidate_vector.stream().mapToInt(i -> i).toArray();
        Best candidate = new Best(cost(candidate_array, cities), candidate_array);

        return candidate;
    }

    public static Best search(double[][] cities, int max_iter, int max_no_improv, double alpha) {
        Best best = null;

        for (int i = 0; i < max_iter; i++) {
            Best candidate = construct_randomized_greedy_solution(cities, alpha);
            candidate = local_search(candidate, cities, max_no_improv);
            if (best == null || (candidate.getCost() < best.getCost())) {
                best = candidate;
            }
            System.out.println(" > Iteration " + (i + 1) + ", best=" + best.getCost());
        }

        return best;
    }

    public static void main(String[] args) {
        double[][] berlin52 = {
            {565, 575}, {25, 185}, {345, 750}, {945, 685}, {845, 655},
            {880, 660}, {25, 230}, {525, 1000}, {580, 1175}, {650, 1130},
            {1605, 620}, {1220, 580}, {1465, 200}, {1530, 5}, {845, 680},
            {725, 370}, {145, 665}, {415, 635}, {510, 875}, {560, 365},
            {300, 465}, {520, 585}, {480, 415}, {835, 625}, {975, 580},
            {1215, 245}, {1320, 315}, {1250, 400}, {660, 180}, {410, 250},
            {420, 555}, {575, 665}, {1150, 1160}, {700, 580}, {685, 595},
            {685, 610}, {770, 610}, {795, 645}, {720, 635}, {760, 650},
            {475, 960}, {95, 260}, {875, 920}, {700, 500}, {555, 815},
            {830, 485}, {1170, 65}, {830, 610}, {605, 625}, {595, 360},
            {1340, 725}, {1740, 245}
        };

        int max_iter = 50;
        int max_no_improv = 50;
        double greediness_factor = 0.3;

        Best best = search(berlin52, max_iter, max_no_improv, greediness_factor);
        System.out.println("Done. Best Solution: c=" + best.getCost() + ", v=" + Arrays.toString(best.getVector()));
    }

}
