-- Create the users
INSERT INTO `user`
(
  `id`,
  `username`,
  `username_canonical`,
  `email`,
  `email_canonical`,
  `salt`,
  `password`,
  `roles`,
  `firstname`,
  `lastname`,
  `message`,
  `status`,
  `creation_date`,
  `photo`
)
VALUES
  (
    10000,              -- Main user for testing
    "test_android_main",
    "test_android_main",
    "test_android_main@msw.com",
    "test_android_main@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_main",
    "test_android",
    "I am test_android_main",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10001,              -- User for testing login with a non-activated account
    "test_android_not_activated",
    "test_android_not_activated",
    "test_android_not_activated@msw.com",
    "test_android_not_activated@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_not_activated",
    "test_android",
    "I am test_android_not_activated",
    "PENDING",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10002,              -- user for testing login with a closed account
    "test_android_closed",
    "test_android_closed",
    "test_android_closed@msw.com",
    "test_android_closed@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_closed",
    "test_android",
    "I am test_android_closed",
    "CLOSED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10011,              -- User as a subscription for the main account
    "test_android_subscription_1",
    "test_android_subscription_1",
    "test_android_subscription_1@msw.com",
    "test_android_subscription_1@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_1",
    "test_android",
    "I am test_android_subscription_1",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10012,              -- User as a subscription for the main account
    "test_android_subscription_2",
    "test_android_subscription_2",
    "test_android_subscription_2@msw.com",
    "test_android_subscription_2@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_2",
    "test_android",
    "I am test_android_subscription_2",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10013,              -- User as a subscription for the main account
    "test_android_subscription_3",
    "test_android_subscription_3",
    "test_android_subscription_3@msw.com",
    "test_android_subscription_3@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_3",
    "test_android",
    "I am test_android_subscription_3",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10014,              -- User as a subscription for the main account
    "test_android_subscription_4",
    "test_android_subscription_4",
    "test_android_subscription_4@msw.com",
    "test_android_subscription_4@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_4",
    "test_android",
    "I am test_android_subscription_4",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10015,              -- User as a subscription for the main account
    "test_android_subscription_5",
    "test_android_subscription_5",
    "test_android_subscription_5@msw.com",
    "test_android_subscription_5@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_5",
    "test_android",
    "I am test_android_subscription_5",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10016,              -- User as a subscription for the main account
    "test_android_subscription_6",
    "test_android_subscription_6",
    "test_android_subscription_6@msw.com",
    "test_android_subscription_6@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_6",
    "test_android",
    "I am test_android_subscription_6",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10017,              -- User as a subscription for the main account
    "test_android_subscription_7",
    "test_android_subscription_7",
    "test_android_subscription_7@msw.com",
    "test_android_subscription_7@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_7",
    "test_android",
    "I am test_android_subscription_7",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10018,              -- User as a subscription for the main account
    "test_android_subscription_8",
    "test_android_subscription_8",
    "test_android_subscription_8@msw.com",
    "test_android_subscription_8@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_8",
    "test_android",
    "I am test_android_subscription_8",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10019,              -- User as a subscription for the main account
    "test_android_subscription_9",
    "test_android_subscription_9",
    "test_android_subscription_9@msw.com",
    "test_androitest_android_subscription_9d_main@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_9",
    "test_android",
    "I am test_android_subscription_9",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10020,              -- User as a subscription for the main account
    "test_android_subscription_10",
    "test_android_subscription_10",
    "test_android_subscription_10@msw.com",
    "test_android_subscription_10@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_10",
    "test_android",
    "I am test_android_subscription_10",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10021,              -- User as a subscription for the main account
    "test_android_subscription_11",
    "test_android_subscription_11",
    "test_android_subscription_11@msw.com",
    "test_android_subscription_11@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_11",
    "test_android",
    "I am test_android_subscription_11",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10022,              -- User as a subscription for the main account
    "test_android_subscription_12",
    "test_android_subscription_12",
    "test_android_subscription_12@msw.com",
    "test_android_subscription_12@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_12",
    "test_android",
    "I am test_android_subscription_12",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10023,              -- User as a subscription for the main account
    "test_android_subscription_13",
    "test_android_subscription_13",
    "test_android_subscription_13@msw.com",
    "test_android_subscription_13@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_13",
    "test_android",
    "I am test_android_subscription_13",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10024,              -- User as a subscription for the main account
    "test_android_subscription_14",
    "test_android_subscription_14",
    "test_android_subscription_14@msw.com",
    "test_android_subscription_14@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_14",
    "test_android",
    "I am test_android_subscription_14",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10025,              -- User as a subscription for the main account
    "test_android_subscription_15",
    "test_android_subscription_15",
    "test_android_subscription_15@msw.com",
    "test_android_subscription_15@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_15",
    "test_android",
    "I am test_android_subscription_15",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10026,              -- User as a subscriber for the main account
    "test_android_subscription_16",
    "test_android_subscription_16",
    "test_android_subscription_16@msw.com",
    "test_android_subscription_16@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_16",
    "test_android",
    "I am test_android_subscription_16",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10027,              -- User as a subscriber for the main account
    "test_android_subscription_17",
    "test_android_subscription_17",
    "test_android_subscription_17@msw.com",
    "test_android_subscription_17@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_17",
    "test_android",
    "I am test_android_subscription_17",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10028,              -- User as a subscriber for the main account
    "test_android_subscription_18",
    "test_android_subscription_18",
    "test_android_subscription_18@msw.com",
    "test_android_subscription_18@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_18",
    "test_android",
    "I am test_android_subscription_18",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10029,              -- User as a subscriber for the main account
    "test_android_subscription_19",
    "test_android_subscription_19",
    "test_android_subscription_19@msw.com",
    "test_android_subscription_19@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_19",
    "test_android",
    "I am test_android_subscription_19",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10030,              -- User as a subscriber for the main account
    "test_android_subscription_20",
    "test_android_subscription_20",
    "test_android_subscription_20@msw.com",
    "test_android_subscription_20@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_20",
    "test_android",
    "I am test_android_subscription_20",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10031,              -- User as a subscriber and subscription for the main account
    "test_android_subscription_21",
    "test_android_subscription_21",
    "test_android_subscription_21@msw.com",
    "test_android_subscription_21@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_21",
    "test_android",
    "I am test_android_subscription_21",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  ),
  (
    10032,              -- User as a subscriber and subscription for the main account
    "test_android_subscription_22",
    "test_android_subscription_22",
    "test_android_subscription_22@msw.com",
    "test_android_subscription_22@msw.com",
    "fffe6sjml68sc0kc0o8so4gos4gogko",
    "$2y$13$fffe6sjml68sc0kc0o8souVtbW/QqzWzfTTfGrLj97pK8PIfquEd.", -- azerty123
    "a:0:{}",
    "test_android_subscription_22",
    "test_android",
    "I am test_android_subscription_22",
    "ACTIVATED",
    NOW(),
    "http://163.5.84.253/images/default_avatar.png"
  );
  
-- Create subscriptions links
INSERT INTO `subscription`
(
  `id`,
  `owner`,
  `subscriber_id`
)
VALUES
  (
    10001,
    10011,
    10000
  ),
  (
    10002,
    10012,
    10000
  ),
  (
    10003,
    10013,
    10000
  ),
  (
    10004,
    10014,
    10000
  ),
  (
    10005,
    10015,
    10000
  ),
  (
    10006,
    10016,
    10000
  ),
  (
    10007,
    10017,
    10000
  ),
  (
    10008,
    10018,
    10000
  ),
  (
    10009,
    10019,
    10000
  ),
  (
    10010,
    10020,
    10000
  ),
  (
    10011,
    10021,
    10000
  ),
  (
    10012,
    10022,
    10000
  ),  
  (
    10013,
    10023,
    10000
  ),
  (
    10014,
    10024,
    10000
  ),
  (
    10015,
    10025,
    10000
  ),
  (
    10016,
    10000,
    10026
  ),
  (
    10017,
    10000,
    10027
  ),
  (
    10018,
    10000,
    10028
  ),
  (
    10019,
    10000,
    10029
  ),  
  (
    10020,
    10000,
    10030
  ),
  (
    10021,
    10000,
    10031
  ),
  (
    10022,
    10031,
    10000
  ),
  (
    10023,
    10000,
    10032
  ),
  (
    10024,
    10032,
    10000
  );