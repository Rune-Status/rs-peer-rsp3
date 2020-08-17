package org.rspeer.environment.account;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.rspeer.game.GameAccount;
import org.rspeer.game.script.Script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

//TODO this impl is temporary, this data will be stored server sided eventually to persist across different systems
public class JsonAccountProvider extends AccountProvider {

    private static final Type ACCOUNT_LIST_TYPE = new TypeToken<List<GameAccount>>() {}.getType();
    private static final String ACCOUNT_FILE_NAME = "account.dat";
    private static final String KEY = "what should we feed sphiinx today?";

    @Override
    protected void load() {
        File file = getFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            byte[] data = Files.readAllBytes(file.toPath());
            String json = new String(xor(data, KEY));
            Gson gson = new Gson();
            List<GameAccount> loaded = gson.fromJson(json, ACCOUNT_LIST_TYPE);
            if (loaded != null) {
                accounts.addAll(loaded);
            }
        } catch (JsonSyntaxException | IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void save() {
        File file = getFile();
        Gson gson = new Gson();
        String json = gson.toJson(accounts, ACCOUNT_LIST_TYPE);
        byte[] data = xor(json.getBytes(), KEY);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private byte[] xor(byte[] data, String key) {
        byte[] result = new byte[data.length];
        byte[] keyByte = key.getBytes();
        for (int x = 0, y = 0; x < data.length; x++, y++) {
            if (y == keyByte.length) {
                y = 0;
            }
            result[x] = (byte) (data[x] ^ keyByte[y]);
        }
        return result;
    }

    private File getFile() {
        return new File(Script.getDataDirectory().toString(), ACCOUNT_FILE_NAME);
    }
}
