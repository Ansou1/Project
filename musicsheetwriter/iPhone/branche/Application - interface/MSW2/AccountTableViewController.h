//
//  AccountTableViewController.h
//  MSW
//
//  Created by simon on 05/02/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AccountTableViewController : UITableViewController

@property (weak, nonatomic) IBOutlet UILabel *Pseudo;
@property (weak, nonatomic) IBOutlet UITextField *Name;
@property (weak, nonatomic) IBOutlet UITextField *Surname;
@property (weak, nonatomic) IBOutlet UITextField *Email;
@property (weak, nonatomic) IBOutlet UITextView *Message;

-(IBAction)logout;

-(IBAction)validez;

@end
