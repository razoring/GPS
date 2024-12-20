import javax.swing.*;
import java.io.File;

public class RelativePathChooser {
    public static void main(String[] args) {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        // Set the current directory to the application's working directory
        fileChooser.setCurrentDirectory(new File("."));
        
        // Show the open dialog
        int result = fileChooser.showOpenDialog(null);
        
        // If a file is selected
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Get the relative path
            String relativePath = new File(".").toURI().relativize(selectedFile.toURI()).getPath();
            // Display the absolute and relative paths
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            System.out.println("Relative path: " + relativePath);
        }
    }
}
