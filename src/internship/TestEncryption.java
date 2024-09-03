package internship;

public class TestEncryption {
    public static void main(String[] args) {
        try {
            String originalData = "erkann";
            String encryptedData = EncryptionUtil.encrypt(originalData);
            String decryptedData = EncryptionUtil.decrypt(encryptedData);

            System.out.println("Orijinal Veri: " + originalData);
            System.out.println("�ifrelenmi� Veri: " + encryptedData);
            System.out.println("��z�len Veri: " + decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
