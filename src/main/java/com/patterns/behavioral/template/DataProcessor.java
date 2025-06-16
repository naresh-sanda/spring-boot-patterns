package com.patterns.behavioral.template;

import org.springframework.stereotype.Component;

/**
 * Template Method Pattern - Defines skeleton of algorithm in base class
 */
abstract class DataProcessor {
    
    // Template method
    public final void processData() {
        readData();
        processDataInternal();
        saveData();
    }
    
    protected abstract void readData();
    protected abstract void processDataInternal();
    protected abstract void saveData();
}

@Component
class CSVDataProcessor extends DataProcessor {
    
    @Override
    protected void readData() {
        System.out.println("Reading data from CSV file");
    }
    
    @Override
    protected void processDataInternal() {
        System.out.println("Processing CSV data");
    }
    
    @Override
    protected void saveData() {
        System.out.println("Saving processed CSV data");
    }
}

@Component
class XMLDataProcessor extends DataProcessor {
    
    @Override
    protected void readData() {
        System.out.println("Reading data from XML file");
    }
    
    @Override
    protected void processDataInternal() {
        System.out.println("Processing XML data");
    }
    
    @Override
    protected void saveData() {
        System.out.println("Saving processed XML data");
    }
}
