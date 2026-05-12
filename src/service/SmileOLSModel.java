package service;

import java.io.Serializable;

public class SmileOLSModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double[] coefficients;
    private final double intercept;
    private final int numFeatures;
    private final int numTrainingSamples;
    private final double rSquared;

    public SmileOLSModel(double[][] X, double[] y) {
        this.numFeatures = X[0].length;
        this.numTrainingSamples = X.length;

        int n = X.length;
        int p = numFeatures;

        double[][] XtX = new double[p][p];
        double[] XtY = new double[p];
        double[] xMeans = new double[p];
        double yMean = 0.0;

        for (int i = 0; i < n; i++) {
            yMean += y[i];
            for (int j = 0; j < p; j++) {
                xMeans[j] += X[i][j];
            }
        }
        yMean /= n;
        for (int j = 0; j < p; j++) {
            xMeans[j] /= n;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < p; j++) {
                double xij = X[i][j] - xMeans[j];
                XtY[j] += xij * (y[i] - yMean);
                for (int k = j; k < p; k++) {
                    double xik = X[i][k] - xMeans[k];
                    XtX[j][k] += xij * xik;
                }
            }
        }

        for (int j = 0; j < p; j++) {
            for (int k = 0; k < j; k++) {
                XtX[j][k] = XtX[k][j];
            }
        }

        double[][] inv = invertMatrix(XtX);
        this.coefficients = new double[p];
        for (int j = 0; j < p; j++) {
            for (int k = 0; k < p; k++) {
                coefficients[j] += inv[j][k] * XtY[k];
            }
        }

        double b = yMean;
        for (int j = 0; j < p; j++) {
            b -= coefficients[j] * xMeans[j];
        }
        this.intercept = b;

        double ssRes = 0.0;
        double ssTot = 0.0;
        for (int i = 0; i < n; i++) {
            double pred = predict(X[i]);
            ssRes += (y[i] - pred) * (y[i] - pred);
            ssTot += (y[i] - yMean) * (y[i] - yMean);
        }
        this.rSquared = ssTot > 0 ? 1.0 - (ssRes / ssTot) : 0.0;
    }

    public double predict(double[] features) {
        double result = intercept;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * features[i];
        }
        return result;
    }

    public double getRSquared() { return rSquared; }
    public int getNumTrainingSamples() { return numTrainingSamples; }
    public int getNumFeatures() { return numFeatures; }
    public double getIntercept() { return intercept; }
    public double[] getCoefficients() { return coefficients.clone(); }

    private static double[][] invertMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] augmented = new double[n][2 * n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, augmented[i], 0, n);
            augmented[i][n + i] = 1.0;
        }

        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(augmented[row][col]) > Math.abs(augmented[maxRow][col])) {
                    maxRow = row;
                }
            }
            double[] temp = augmented[col];
            augmented[col] = augmented[maxRow];
            augmented[maxRow] = temp;

            double pivot = augmented[col][col];
            if (Math.abs(pivot) < 1e-12) {
                pivot = 1e-12;
            }
            for (int j = 0; j < 2 * n; j++) {
                augmented[col][j] /= pivot;
            }

            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = augmented[row][col];
                    for (int j = 0; j < 2 * n; j++) {
                        augmented[row][j] -= factor * augmented[col][j];
                    }
                }
            }
        }

        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmented[i], n, inverse[i], 0, n);
        }
        return inverse;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SmileOLSModel {");
        sb.append(" features=").append(numFeatures);
        sb.append(", samples=").append(numTrainingSamples);
        sb.append(", R²=").append(String.format("%.4f", rSquared));
        sb.append(", intercept=").append(String.format("%.4f", intercept));
        sb.append(", coefficients=[");
        String[] names = {"cgpaRatio", "skillOverlap", "experienceMatch", "locationMatch", "cosineSimilarity"};
        for (int i = 0; i < coefficients.length; i++) {
            if (i > 0) sb.append(", ");
            String name = i < names.length ? names[i] : "x" + i;
            sb.append(name).append("=").append(String.format("%.4f", coefficients[i]));
        }
        sb.append("] }");
        return sb.toString();
    }
}
