package fr.etudes.ps6_final_otake.mocks;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.models.CustomSpinnerOfficeItem;

public class OfficeItemsMock {

    private ArrayList<CustomSpinnerOfficeItem> officeItems = new ArrayList<>();

    public OfficeItemsMock(){
        init();
    }

    private void init(){
        officeItems.add(new CustomSpinnerOfficeItem("RI GE - M.Santisi",true, "25", "5"));
        officeItems.add(new CustomSpinnerOfficeItem("RI ELEC - M. Bilavran",false, "30", "6"));
        officeItems.add(new CustomSpinnerOfficeItem("RI GB - Mme. Cupo",true, "40", "8"));
        officeItems.add(new CustomSpinnerOfficeItem("RI GB - M. Macia",false, "10", "2"));
        officeItems.add(new CustomSpinnerOfficeItem("RI GE - M. Brigode",false, "0", "0"));
        officeItems.add(new CustomSpinnerOfficeItem("RI MAM - M. Habbal",true, "30", "6"));
        officeItems.add(new CustomSpinnerOfficeItem("RI SI - Mme. Pinna",true, "0", "0"));
        officeItems.add(new CustomSpinnerOfficeItem("BRI - Mme. Maiffret",true, "15", "3"));
        officeItems.add(new CustomSpinnerOfficeItem("BRI - Mme. winchcombe",true, "5", "1"));
    }

    public ArrayList<CustomSpinnerOfficeItem> getOfficeItems(){
        return officeItems;
    }
}
