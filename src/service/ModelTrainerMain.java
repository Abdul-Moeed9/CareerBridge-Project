package service;

public class ModelTrainerMain {

    public static void main(String[] args) {
        System.out.println("=== CareerBridge Model Trainer ===");
        System.out.println("Connecting to database...");

        AIMatchingService service = new AIMatchingService();

        if (AIMatchingService.isModelTrained()) {
            System.out.println("Existing model found. Retraining with fresh data...");
        } else {
            System.out.println("No existing model. Training new model...");
        }

        service.trainFromDatabase();

        if (AIMatchingService.isModelTrained()) {
            System.out.println("=== Training complete. Model saved to: careerbridge_model.model ===");
        } else {
            System.err.println("=== Training failed. Check database connectivity and data. ===");
        }
    }
}
