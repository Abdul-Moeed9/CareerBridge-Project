package domain;

import java.io.*;
import java.util.Date;
import java.util.List;

public abstract class ModelTrainer {

    protected Object AIModel;
    protected List<Object> trainingData;
    protected String modelPath;
    protected Date lastTrained;
    protected JobFeed jobFeed;

    public abstract void train(List<Object> data);
    public abstract double evaluate();

    public void loadModel(String modelPath) {
        this.modelPath = modelPath;
        if (modelPath != null && !modelPath.isEmpty()) {
            File f = new File(modelPath);
            if (f.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                    this.AIModel = ois.readObject();
                } catch (Exception e) {
                    System.err.println("Failed to load model from " + modelPath + ": " + e.getMessage());
                }
            }
        }
    }

    public void saveModel(String modelPath) {
        this.modelPath = modelPath;
        if (this.AIModel != null && modelPath != null && !modelPath.isEmpty()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
                oos.writeObject(this.AIModel);
            } catch (Exception e) {
                System.err.println("Failed to save model to " + modelPath + ": " + e.getMessage());
            }
        }
    }

    public String getModelPath() { return modelPath; }
    public Date getLastTrained() { return lastTrained; }

    public JobFeed getJobFeed() { return jobFeed; }
    public void setJobFeed(JobFeed jobFeed) { this.jobFeed = jobFeed; }
}
