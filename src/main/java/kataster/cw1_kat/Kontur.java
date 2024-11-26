package kataster.cw1_kat;

import java.util.ArrayList;
import java.util.Arrays;

public class Kontur {
    private String OFU;
    private String OZU;
    private final ArrayList<String> notClassifiable = new ArrayList<>(Arrays.asList(
            "B", "Ba", "Bi", "Bp", "Bz", "K", "dr", "Tk", "Ti", "Tp",
            "Wm", "Wp", "Ws", "Tr", "N"
    ));

    private final ArrayList<String> OFUValues = new ArrayList<>(Arrays.asList(
            "R", "S", "Br", "Wsr", "W", "Lzr", "Ł" //klasyfikowane + Ł?
    ));

    private final ArrayList<String> OZUValues = new ArrayList<>(Arrays.asList(
            "Ł", "Ps", "Ls", "Lz", "R" // bez myslnikow ofu = ozu i te wartosci
    ));

    private final ArrayList<String> OZKClassification1 = new ArrayList<>(Arrays.asList(
            "I", "II", "III", "IV", "V", "VI"
    ));

    private final ArrayList<String> OZKClassification2 = new ArrayList<>(Arrays.asList(
            "I", "II", "IIIa", "IIIb", "IVa", "IVb", "V", "VI", "VIz"
    ));

    public Kontur(String OFU, String OZU) {
        this.OFU = OFU;
        this.OZU = OZU;
    }

    public Kontur(String OFU) {
        this.OFU = OFU;
    }

    public String getStatus(){
        //returns empty string if the contour is correct, otherwise the message indicating an error in contour format.
        String status = "";
        if(this.OZU == null){ //Kontur was initialized with single parameter (name is not in format e.g. Ltr-RIIIa)
            status = checkSinglePartContour();
        }
        else{
            status = checkTwoPartContour();
        }
        return status;
    }

    private String checkSinglePartContour(){
        String onlyPart = this.OFU;
        if(onlyPart.contains(" ")){
            return "Nieprawidłowy format konturu: " + onlyPart +", w identyfikatorze powinien znaleźć się symbol '-'.";
        }

        int splitIndex = getIndexOfClassificationBeginning(onlyPart);
        if(splitIndex != -1){
            String id = onlyPart.substring(0, splitIndex);
            String classification = onlyPart.substring(splitIndex);
            String contourStatus = checkTheClassification(id, classification);
            if(!contourStatus.isEmpty()){
                return contourStatus;
            }
        }
        else{
            if(this.OFUValues.contains(onlyPart)){
                String classification = onlyPart.equals("R") ? arrayListToString(OZKClassification2) : arrayListToString(OZKClassification1);
                return "Dla OZU: " + onlyPart + " powinna być zastosowana klasyfikacja: " + classification;
            }
            if(!this.notClassifiable.contains(onlyPart)){
                return "Niepoprawny skrót OZU: " + onlyPart + ".";
            }
        }
        return "";
    }

    private int getIndexOfClassificationBeginning(String classificationString){
        int index = Integer.MAX_VALUE;
        if(classificationString.contains("I")){
            index = classificationString.indexOf("I");
        }
        if(classificationString.contains("V")){
            index = Math.min(classificationString.indexOf("V"), index);
        }
        return (index==Integer.MAX_VALUE) ? -1 : index;
    }

    private String checkTwoPartContour(){
        String firstPart = this.OFU;
        String secondPart = this.OZU;

        if(this.notClassifiable.contains(firstPart)){
            return "Niepoprawny skrót OFU: " + firstPart + ". Skróty OFU: " + arrayListToString(this.notClassifiable) + " nie podlegają klasyfikacji.";
        }

        if(!this.OFUValues.contains(firstPart)){
            return "Niepoprawny skrót OFU: " +firstPart+ "."
                    +" Skróty OFU, które mogą wystąpić w dwuczłonowym identyfikatorze: "+arrayListToString(this.OFUValues);
        }

        int splitIndex = getIndexOfClassificationBeginning(secondPart);
        if(splitIndex == -1){
            if(!this.OZUValues.contains(secondPart)){
                return "Dla OFU: " + firstPart + ", OZU: " + secondPart + " jest niepoprawne." + " OZU powinno być jednym ze skrótów: " + arrayListToString(this.OZUValues);
            }
        }
        else{
            String id = secondPart.substring(0, splitIndex);
            String classification = secondPart.substring(splitIndex);
            String contourStatus = checkTheClassification(id, classification);
            if(!contourStatus.isEmpty()){
                return contourStatus;
            }
        }
        return "";
    }

    private String checkTheClassification(String id, String classification){
        if(!this.OZUValues.contains(id)){
            return "Niepoprawna wartość OZU: " + id + ", " + "nie należy do skrótów podlegających klasyfikacji określonych w ustawie." + "Poprawne OZU: " + arrayListToString(OZUValues);
        }

        if(id.equals("R")){
            if(!this.OZKClassification2.contains(classification)){
                return "Niepoprawna klasyfikacja: " + classification + ", dla OZU: " + id + "." + " Poprawna klasyfikacja: "+ arrayListToString(this.OZKClassification2);
            }
        }
        else{
            if(!this.OZKClassification1.contains(classification)){
                return "Niepoprawna klasyfikacja: " + classification + ", dla OZU: " + id+ "." + " Poprawna klasyfikacja: "+ arrayListToString(this.OZKClassification1);
            }
        }
        return "";
    }

    private String arrayListToString(ArrayList<String> arr){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String str : arr){
            sb.append(str);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }
}
