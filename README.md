# Wraith King

![Wraith King](https://cdnb.artstation.com/p/assets/images/images/007/213/771/large/yunfeng-zhang-loading-001.jpg)

A Dota 2 hero that self-resurrecting ultimate ensures he'll have another chance to swing his sword
– https://www.dota2.com/hero/wraithking

## DLQs

Kafka messages that for some reason/problem couldn't be consumed, these messages are added to the DLQ queue were you are
able to check the stack-trace from the root cause for the message not being consumed, from that you can decide if you
want to replay the message and the consumer service will try to process it one more time. We should have in mind that
when using DLQs the consumers need to support process messages in any order without affecting bussiness logic.

## License

Copyright © 2022 Bruno do Nascimento Maciel

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which
is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary Licenses when the conditions for such
availability set forth in the Eclipse Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your option) any later version, with the GNU
Classpath Exception which is available at https://www.gnu.org/software/classpath/license.html.
