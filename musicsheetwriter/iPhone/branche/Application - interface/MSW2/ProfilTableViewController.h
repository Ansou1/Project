//
//  ProfilTableViewController.h
//  MSW
//
//  Created by simon on 17/02/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProfilTableViewController : UITableViewController

@property (weak, nonatomic) IBOutlet UILabel *pseudo;
@property (weak, nonatomic) IBOutlet UILabel *surname;
@property (weak, nonatomic) IBOutlet UILabel *name;
@property (weak, nonatomic) IBOutlet UILabel *email;
@property (weak, nonatomic) IBOutlet UITextView *Message;

@property(strong, nonatomic) NSString *IdProfil;

@end
