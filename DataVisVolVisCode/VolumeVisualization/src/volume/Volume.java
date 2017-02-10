/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author michel
 * @Anna 
 * Volume object: This class contains the object and assumes that the distance between the voxels in x,y and z are 1 
 */
public class Volume {
    
    public Volume(int xd, int yd, int zd) {
        data = new short[xd*yd*zd];
        dimX = xd;
        dimY = yd;
        dimZ = zd;
    }
    
    public Volume(File file) {
        
        try {
            VolumeIO reader = new VolumeIO(file);
            dimX = reader.getXDim();
            dimY = reader.getYDim();
            dimZ = reader.getZDim();
            data = reader.getData().clone();
            computeHistogram();
        } catch (IOException ex) {
            System.out.println("IO exception");
        }
        
    }
    
    
    public short getVoxel(int x, int y, int z) {
        return data[x + dimX*(y + dimY * z)];
    }
    
    public void setVoxel(int x, int y, int z, short value) {
        data[x + dimX*(y + dimY*z)] = value;
    }

    public void setVoxel(int i, short value) {
        data[i] = value;
    }
    
    public short getVoxelInterpolate(double[] coord) {
    /* to be implemented: get the trilinear interpolated value. 
        The current implementation gets the Nearest Neightbour */
    
        int x = (int) Math.floor(coord[0]);
        int y = (int) Math.floor(coord[1]);
        int z = (int) Math.floor(coord[2]);
        
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1)
                || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return 0;
        }
        /* notice that in this framework we assume that the distance between neighbouring voxels is 1 in all directions*/
        /*
        int x = (int) Math.round(coord[0]); 
        int y = (int) Math.round(coord[1]);
        int z = (int) Math.round(coord[2]);
        */
        
        int val0 = getVoxel(x, y, z);
        int val1 = getVoxel(x + 1, y, z);
        
        int val2 = getVoxel(x, y + 1, z);
        int val3 = getVoxel(x + 1, y + 1, z);
        
        int val4 = getVoxel(x, y, z + 1);
        int val5 = getVoxel(x + 1, y, z + 1);
        
        int val6 = getVoxel(x, y + 1, z + 1);
        int val7 = getVoxel(x + 1, y + 1, z + 1);
        
        double x_diff = coord[0] - x;
        double y_diff = coord[1] - y;
        double z_diff = coord[2] - z;
        // four linear interpolation
        double val01 = x_diff * val1 + (1 - x_diff) * val0;
        double val23 = x_diff * val3 + (1 - x_diff) * val2;
        double val45 = x_diff * val5 + (1 - x_diff) * val4;
        double val67 = x_diff * val7 + (1 - x_diff) * val6;
        // two bi-linear interpolation
        double val0123 = y_diff * val23 + (1 - y_diff) * val01;
        double val4567 = y_diff * val67 + (1 - y_diff) * val45;
        // one tri-linear interpolation
        double finalVal = z_diff * val4567 + (1 - z_diff) * val0123;
        
        return (short)Math.round(finalVal);
        
    }
    
    public short getVoxel(int i) {
        return data[i];
    }
    
    public int getDimX() {
        return dimX;
    }
    
    public int getDimY() {
        return dimY;
    }
    
    public int getDimZ() {
        return dimZ;
    }

    public short getMinimum() {
        short minimum = data[0];
        for (int i=0; i<data.length; i++) {
            minimum = data[i] < minimum ? data[i] : minimum;
        }
        return minimum;
    }

    public short getMaximum() {
        short maximum = data[0];
        for (int i=0; i<data.length; i++) {
            maximum = data[i] > maximum ? data[i] : maximum;
        }
        return maximum;
    }
 
    public int[] getHistogram() {
        return histogram;
    }
    
    private void computeHistogram() {
        histogram = new int[getMaximum() + 1];
        for (int i=0; i<data.length; i++) {
            histogram[data[i]]++;
        }
    }
    
    private int dimX, dimY, dimZ;
    private short[] data;
    private int[] histogram;
}
