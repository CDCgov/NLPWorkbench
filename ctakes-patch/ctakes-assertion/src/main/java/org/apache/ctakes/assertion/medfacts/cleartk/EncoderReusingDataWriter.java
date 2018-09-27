package org.apache.ctakes.assertion.medfacts.cleartk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import org.cleartk.ml.encoder.outcome.StringToIntegerOutcomeEncoder;
import org.cleartk.ml.liblinear.LibLinearStringOutcomeDataWriter;
import org.cleartk.ml.liblinear.encoder.FeatureNodeArrayEncoder;

public class EncoderReusingDataWriter extends LibLinearStringOutcomeDataWriter {

  public EncoderReusingDataWriter(File outputDirectory)
      throws FileNotFoundException {
    super(outputDirectory);
    File encoderFile = new File(outputDirectory, "encoders.ser");
    if(encoderFile.exists()){
      try {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(encoderFile));
        this.setFeaturesEncoder((FeatureNodeArrayEncoder) ois.readObject());
        ois.close();
      } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
        throw new FileNotFoundException("Problem loading encoder from encoders.ser");
      }
    }
    
    File outputEncoderFile = new File(outputDirectory, "outcome-lookup.txt");
    if(outputEncoderFile.exists()){
      StringToIntegerOutcomeEncoder outcomeEncoder = new StringToIntegerOutcomeEncoder();
      try(Scanner scanner = new Scanner(outputEncoderFile)){
        String line;
        while(scanner.hasNextLine()){
          line = scanner.nextLine();
          String[] ind_val = line.split(" ");
          outcomeEncoder.encode(ind_val[1]);
        }
      }
      this.setOutcomeEncoder(outcomeEncoder);
    }
  }

}
