import java.util.ArrayList;
import java.util.Date;

public class Main {

    private static Point p1, p2, p3, p4;
    private static Heater h1, h2, h3, h4;
    private static ArrayList<ArrayList<ArrayList<Double>>> z_c1;
    private static ArrayList<ArrayList<ArrayList<Double>>> z_c2;
    private static ArrayList<Double> points_temps;
    private static ArrayList<Double> best_match;
    private static Date start_date, end_date;
    private static double d_tau;
    private static double d_x, d_y, d_z;
    private static double d_h;
    private static double x_max, y_max, z_max;
    private static double a;
    private static double start_coef;
    private static double first_p, second_p, third_p, fourth_p;
    private static double first_h, second_h, third_h, fourth_h;
    private static double lx, ly, lz;
    private static double h_max;
    private static double tau_max;
    private static double best_match_tau;
    private static double standard_deviation;

    public static void main(String[] args) {
        execute();
    }

    private static void execute() {
        setACoef();
        setStartCoef();
        setDimensions();
        setRequiredTemps();
        setHMax();
        setTauMax();
        setTimeSamplingStep();
        setCoordsSamplingSteps();
        setHeatersSamplingStep();
        calculateAmountsOfDots();
        initializePoints();
        setPointsCoords();
        initializeHeaters();
        setHeatersCoords();
        setStartPointsTemps();
        calculateStandardDev();
        setStartBestMatch();
        printStartTime();
        start();
        printEndTime();
        printComputingTime();
        printBestMatch();
    }

    private static void start() {
        int start_temp = (int) (getMinRequired() * start_coef);
        for (int i = start_temp; i <= (int) h_max; i += (int) d_h) {
            for (int j = start_temp; j <= (int) h_max; j += (int) d_h) {
                for (int k = start_temp; k <= (int) h_max; k += (int) d_h) {
                    for (int l = start_temp; l <= (int) h_max; l += (int) d_h) {
                        z_c1 = initializeCollection();
                        z_c2 = initializeCollection();
                        setHeatersTemps(i, j, k, l);
                        onHeaters();
                        calculate();
                        calculateStandardDev();
                        if (standard_deviation < best_match.get(9)) {
                            best_match.set(0, points_temps.get(0));
                            best_match.set(1, points_temps.get(1));
                            best_match.set(2, points_temps.get(2));
                            best_match.set(3, points_temps.get(3));
                            best_match.set(4, (double) i);
                            best_match.set(5, (double) j);
                            best_match.set(6, (double) k);
                            best_match.set(7, (double) l);
                            best_match.set(8, best_match_tau);
                            best_match.set(9, standard_deviation);
                        }
                        System.out.println(calculatePercentage(start_temp, (int) h_max, i, j, k ,l) + "%");
                    }
                }
            }
        }
    }

    private static void printStartTime() {
        start_date = new Date();
        System.out.println("\nStart time: " + start_date);
    }

    private static void printEndTime() {
        end_date = new Date();
        System.out.println("End time: " + end_date);
    }

    private static void printComputingTime() {
        System.out.println("The computing lasted " + (end_date.getTime() - start_date.getTime()) / 1000 + " seconds");
    }

    private static void printBestMatch() {
        System.out.println("\nBest match:");
        for (int i = 0; i < 4; i++) {
            System.out.print(best_match.get(i) + "\t");
        }
        System.out.println();
        for (int i = 4; i < 8; i++) {
            System.out.print(best_match.get(i) + "\t");
        }
        System.out.println("\n" + best_match.get(8));
        System.out.println(best_match.get(9));
    }

    private static double calculatePercentage(int min, int max, int i, int j, int k, int l) {
        double iterations = Math.pow(1 + (max - min) / d_h, 4);
        double passed;
        if ((i == max) && (j == max) && (k == max) && (l == max)) {
            passed = iterations;
        } else {
            passed = ((i - min) / d_h) * Math.pow(1 + (max - min) / d_h, 3) +
                    ((j - min) / d_h) * Math.pow(1 + (max - min) / d_h, 2) +
                    ((k - min) / d_h) * (1 + (max - min) / d_h) +
                    (l - min) / d_h;
        }
        return 100 * passed / iterations;
    }

    private static ArrayList<Double> initializePointsTemps() {
        ArrayList<Double> points_temps = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            points_temps.add(0.0);
        }
        return points_temps;
    }

    private static void setStartPointsTemps() {
        points_temps = initializePointsTemps();
    }

    private static ArrayList<Double> initializeBestMatch() {
        ArrayList<Double> best_match = new ArrayList<>(10);
        for (int i = 0; i < 9; i++) {
            best_match.add(0.0);
        }
        best_match.add(standard_deviation);
        return best_match;
    }

    private static void setStartBestMatch() {
        best_match = initializeBestMatch();
    }

    private static void initializePoints() {
        p1 = new Point();
        p2 = new Point();
        p3 = new Point();
        p4 = new Point();
    }

    private static void setPointsCoords() {
        p1.setCoordinates(0.24, 0.12);
        p2.setCoordinates(0.24, 0.28);
        p3.setCoordinates(0.56, 0.12);
        p4.setCoordinates(0.56, 0.28);
    }

    private static void initializeHeaters() {
        h1 = new Heater();
        h2 = new Heater();
        h3 = new Heater();
        h4 = new Heater();
    }

    private static void setHeatersCoords() {
        h1.setCoordinates(0.24, 0.12);
        h2.setCoordinates(0.24, 0.28);
        h3.setCoordinates(0.56, 0.12);
        h4.setCoordinates(0.56, 0.28);
    }

    private static double getMinRequired() {
        return Math.min(Math.min(first_p, second_p), Math.min(third_p, fourth_p));
    }

    private static ArrayList<ArrayList<ArrayList<Double>>> initializeCollection() {
        ArrayList<ArrayList<ArrayList<Double>>> z_c = new ArrayList<>((int) z_max);
        for (int i = 0; i < (int) z_max; i++) {
            ArrayList<ArrayList<Double>> y = new ArrayList<>((int) y_max);
            for (int j = 0; j < (int) y_max; j++) {
                ArrayList<Double> x = new ArrayList<>((int) x_max);
                for (int k = 0; k < (int) x_max; k++) {
                    x.add((double) 0);
                }
                y.add(x);
            }
            z_c.add(y);
        }
        return z_c;
    }

    private static void onHeaters() {
        z_c1.get(((int) z_max) - 1).get((int) (h1.getY() / d_y)).set((int) (h1.getX() / d_x), first_h);
        z_c1.get(((int) z_max) - 1).get((int) (h2.getY() / d_y)).set((int) (h2.getX() / d_x), second_h);
        z_c1.get(((int) z_max) - 1).get((int) (h3.getY() / d_y)).set((int) (h3.getX() / d_x), third_h);
        z_c1.get(((int) z_max) - 1).get((int) (h4.getY() / d_y)).set((int) (h4.getX() / d_x), fourth_h);
        z_c2.get(((int) z_max) - 1).get((int) (h1.getY() / d_y)).set((int) (h1.getX() / d_x), first_h);
        z_c2.get(((int) z_max) - 1).get((int) (h2.getY() / d_y)).set((int) (h2.getX() / d_x), second_h);
        z_c2.get(((int) z_max) - 1).get((int) (h3.getY() / d_y)).set((int) (h3.getX() / d_x), third_h);
        z_c2.get(((int) z_max) - 1).get((int) (h4.getY() / d_y)).set((int) (h4.getX() / d_x), fourth_h);
    }

    private static void calculate() {
        for (int i = 0; i < (int) tau_max; i += d_tau) {
            calculateObject(i % 2);
            if ((i > 59) && (isSteady())) {
                getActualTemps(i % 2);
                best_match_tau = i;
                break;
            }
            if (i >= (int) tau_max - 1) {
                getActualTemps(i % 2);
                best_match_tau = i;
            }
        }
    }

    private static void getActualTemps(int i) {
        if (i % 2 == 0) {
            points_temps.set(0,
                    z_c2.get((int) (p1.getZ() / d_z)).get((int) (p1.getY() / d_y)).get((int) (p1.getX() / d_x)));
            points_temps.set(1,
                    z_c2.get((int) (p2.getZ() / d_z)).get((int) (p2.getY() / d_y)).get((int) (p2.getX() / d_x)));
            points_temps.set(2,
                    z_c2.get((int) (p3.getZ() / d_z)).get((int) (p3.getY() / d_y)).get((int) (p3.getX() / d_x)));
            points_temps.set(3,
                    z_c2.get((int) (p4.getZ() / d_z)).get((int) (p4.getY() / d_y)).get((int) (p4.getX() / d_x)));
        } else {
            points_temps.set(0,
                    z_c1.get((int) (p1.getZ() / d_z)).get((int) (p1.getY() / d_y)).get((int) (p1.getX() / d_x)));
            points_temps.set(1,
                    z_c1.get((int) (p2.getZ() / d_z)).get((int) (p2.getY() / d_y)).get((int) (p2.getX() / d_x)));
            points_temps.set(2,
                    z_c1.get((int) (p3.getZ() / d_z)).get((int) (p3.getY() / d_y)).get((int) (p3.getX() / d_x)));
            points_temps.set(3,
                    z_c1.get((int) (p4.getZ() / d_z)).get((int) (p4.getY() / d_y)).get((int) (p4.getX() / d_x)));
        }
    }

    private static void calculateStandardDev() {
        standard_deviation = Math.sqrt((Math.pow(points_temps.get(0) - first_p, 2) +
                Math.pow(points_temps.get(1) - second_p, 2) +
                Math.pow(points_temps.get(2) - third_p, 2) +
                Math.pow(points_temps.get(3) - fourth_p, 2)) / 3);
    }

    private static void calculateObject(int number) {
        if (number == 0) {
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 1; j < ((int) y_max) - 1; j++) {
                    for (int k = 1; k < ((int) x_max) - 1; k++) {
                        z_c2.get(i).get(j).set(k, calculateDot(i, j, k, number));
                    }
                }
            }
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 0; j < (int) y_max; j++) {
                    z_c2.get(i).get(j).set(0, z_c2.get(i).get(j).get(1));
                }
            }
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 0; j < ((int) x_max) - 1; j++) {
                    z_c2.get(i).get(0).set(j, z_c2.get(i).get(1).get(j));
                }
            }
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 0; j < ((int) x_max) - 1; j++) {
                    z_c2.get(i).get(((int) y_max) - 1).set(j, z_c2.get(i).get(((int) y_max) - 2).get(j));
                }
            }
        } else {
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 1; j < ((int) y_max) - 1; j++) {
                    for (int k = 1; k < ((int) x_max) - 1; k++) {
                        z_c1.get(i).get(j).set(k, calculateDot(i, j, k, number));
                    }
                }
            }
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 0; j < (int) y_max; j++) {
                    z_c1.get(i).get(j).set(0, z_c1.get(i).get(j).get(1));
                }
            }
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 0; j < ((int) x_max) - 1; j++) {
                    z_c1.get(i).get(0).set(j, z_c1.get(i).get(1).get(j));
                }
            }
            for (int i = 1; i < ((int) z_max) - 1; i++) {
                for (int j = 0; j < ((int) x_max) - 1; j++) {
                    z_c1.get(i).get(((int) y_max) - 1).set(j, z_c1.get(i).get(((int) y_max) - 2).get(j));
                }
            }
        }
    }

    private static boolean isSteady() {
        boolean steady;
        double d1, d2, d3, d4;
        d1 = Math.abs(z_c1.get((int) (p1.getZ() / d_z)).get((int) (p1.getY() / d_y)).get((int) (p1.getX() / d_x)) -
                z_c2.get((int) (p1.getZ() / d_z)).get((int) (p1.getY() / d_y)).get((int) (p1.getX() / d_x)));
        d2 = Math.abs(z_c1.get((int) (p2.getZ() / d_z)).get((int) (p2.getY() / d_y)).get((int) (p2.getX() / d_x)) -
                z_c2.get((int) (p2.getZ() / d_z)).get((int) (p2.getY() / d_y)).get((int) (p2.getX() / d_x)));
        d3 = Math.abs(z_c1.get((int) (p3.getZ() / d_z)).get((int) (p3.getY() / d_y)).get((int) (p3.getX() / d_x)) -
                z_c2.get((int) (p3.getZ() / d_z)).get((int) (p3.getY() / d_y)).get((int) (p3.getX() / d_x)));
        d4 = Math.abs(z_c1.get((int) (p4.getZ() / d_z)).get((int) (p4.getY() / d_y)).get((int) (p4.getX() / d_x)) -
                z_c2.get((int) (p4.getZ() / d_z)).get((int) (p4.getY() / d_y)).get((int) (p4.getX() / d_x)));
        steady = (d1 <= 0.001) && (d2 <= 0.001) && (d3 <= 0.001) && (d4 <= 0.001);
        return steady;
    }

    private static double calculateDot(int z, int y, int x, int number) {
        double value;
        if (number == 0) {
            value = z_c1.get(z).get(y).get(x) + a * d_tau * (
                    (z_c1.get(z).get(y).get(x - 1) - 2 * z_c1.get(z).get(y).get(x)
                            + z_c1.get(z).get(y).get(x + 1)) / Math.pow(d_x, 2) +
                            (z_c1.get(z).get(y - 1).get(x) - 2 * z_c1.get(z).get(y).get(x)
                                    + z_c1.get(z).get(y + 1).get(x)) / Math.pow(d_y, 2) +
                            (z_c1.get(z - 1).get(y).get(x) - 2 * z_c1.get(z).get(y).get(x)
                                    + z_c1.get(z + 1).get(y).get(x)) / Math.pow(d_z, 2));
        } else {
            value = z_c2.get(z).get(y).get(x) + a * d_tau * (
                    (z_c2.get(z).get(y).get(x - 1) - 2 * z_c2.get(z).get(y).get(x)
                            + z_c2.get(z).get(y).get(x + 1)) / Math.pow(d_x, 2) +
                            (z_c2.get(z).get(y - 1).get(x) - 2 * z_c2.get(z).get(y).get(x)
                                    + z_c2.get(z).get(y + 1).get(x)) / Math.pow(d_y, 2) +
                            (z_c2.get(z - 1).get(y).get(x) - 2 * z_c2.get(z).get(y).get(x)
                                    + z_c2.get(z + 1).get(y).get(x)) / Math.pow(d_z, 2));
        }
        return value;
    }

    private static void setRequiredTemps() {
        Main.first_p = 100;
        Main.second_p = 70;
        Main.third_p = 50;
        Main.fourth_p = 70;
    }

    private static void setHeatersTemps(double first_h, double second_h, double third_h, double fourth_h) {
        Main.first_h = first_h;
        Main.second_h = second_h;
        Main.third_h = third_h;
        Main.fourth_h = fourth_h;
    }

    private static void setDimensions() {
        Main.lx = 0.8;
        Main.ly = 0.4;
        Main.lz = 0.3;
    }

    private static void setACoef() {
        Main.a = 8.4E-5;
    }

    private static void setStartCoef() {
        Main.start_coef = 65;
    }

    private static void setHMax() {
        Main.h_max = 3300;
    }

    private static void setTauMax() {
        Main.tau_max = 3600;
    }

    private static void setTimeSamplingStep() {
        Main.d_tau = 1;
    }

    private static void setCoordsSamplingSteps() {
        Main.d_x = 0.08;
        Main.d_y = 0.04;
        Main.d_z = 0.03;
    }

    private static void setHeatersSamplingStep() {
        Main.d_h = 25;
    }

    private static void calculateAmountsOfDots() {
        x_max = lx / d_x;
        y_max = ly / d_y;
        z_max = lz / d_z;
    }
}


class Point {
    private double x, y, z;

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    double getZ() {
        return z;
    }

    void setCoordinates(double x, double y) {
        Point.this.x = x;
        Point.this.y = y;
        Point.this.z = 0.09;
    }
}


class Heater {
    private double x, y;

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    void setCoordinates(double x, double y) {
        Heater.this.x = x;
        Heater.this.y = y;
    }
}
