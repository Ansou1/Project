-- Delete email confirmations from test users
DELETE FROM `email_confirm` 
  WHERE `user_id` IN (SELECT `id`FROM `user` WHERE `username` LIKE "test_android_%");

-- Delete password reinitialization links from test users
DELETE FROM `password_link`
  WHERE `user_id` IN (SELECT `id`FROM `user` WHERE `username` LIKE "test_android_%");

-- Delete subscriptions from test users
DELETE FROM `subscription`
  WHERE `owner` IN (SELECT `id`FROM `user` WHERE `username` LIKE "test_android_%")
    OR `subscriber_id` IN (SELECT `id`FROM `user` WHERE `username` LIKE "test_android_%");

-- Delete the users
DELETE FROM `user` WHERE username LIKE "test_android_%";