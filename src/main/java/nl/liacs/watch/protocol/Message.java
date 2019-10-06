package nl.liacs.watch.protocol;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Scanner;

public class Message {
    public MessageType type;
    public ArrayList<MessageParameter> parameters;

    public String encode() {
        StringBuilder sb = new StringBuilder();

        sb.append(type.name());
        sb.append('\t');

        sb.append(parameters.size());

        for (MessageParameter parameter : parameters) {
            sb.append('\t');
            sb.append(parameter.toString());
        }

        return sb.toString();
    }

    static Message decode(String str) throws Exception {
        Scanner sc = new Scanner(str);
        String typeName = sc.next();

        int nParams = sc.nextInt();
        ArrayList<MessageParameter> params = new ArrayList<>(nParams);
        while (sc.hasNext()) {
            var paramRaw = sc.next().split("!");
            var paramType = ParameterType.valueOf(paramRaw[0]);

            Object paramValue;
            switch (paramType) {
            case DOUBLE:
                paramValue = Double.parseDouble(paramRaw[1]);
                break;
            case INTEGER:
                paramValue = Integer.parseInt(paramRaw[1]);
                break;
            case STRING:
                paramValue = paramRaw[1];
                break;

            default:
                throw new Exception("unknown parameter type");
            }

            var param = new MessageParameter();
            param.type = paramType;
            param.value = paramValue;
            params.add(param);
        }

        var msg = new Message();
        msg.type = MessageType.valueOf(typeName);
        msg.parameters = params;
        return msg;
    }
}
