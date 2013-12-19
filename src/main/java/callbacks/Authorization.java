/*
 * Copyright 2013 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package callbacks;

import com.dcsquare.hivemq.spi.callback.CallbackPriority;
import com.dcsquare.hivemq.spi.callback.security.OnAuthorizationCallback;
import com.dcsquare.hivemq.spi.security.ClientData;
import com.dcsquare.hivemq.spi.topic.MqttTopicPermission;
import com.google.inject.Inject;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.group.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lukas Brandl
 */
public class Authorization implements OnAuthorizationCallback {

    Application application;

    @Inject
    public Authorization(Application application) {
        this.application = application;
    }

    @Override
//    @Cached(timeToLive = 10, timeUnit = TimeUnit.MINUTES)
    public List<MqttTopicPermission> getPermissionsForClient(ClientData clientData) {
        Account account = null;

        AccountList accountList = application.getAccounts(Accounts.where(Accounts.username().eqIgnoreCase(clientData.getUsername().get())));
        Iterator<Account> iterator = accountList.iterator();
        if (iterator.hasNext()) {
            account = iterator.next();
        }

        List<MqttTopicPermission> mqttTopicPermissions = new ArrayList<MqttTopicPermission>();
        if (account != null) {
            for (Group group : account.getGroups()) {
                mqttTopicPermissions.add(new MqttTopicPermission(group.getName()));
            }
        }

        return mqttTopicPermissions;
    }

    @Override
    public int priority() {
        return CallbackPriority.MEDIUM;
    }
}
