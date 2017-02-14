//
//  ContactViewController.h
//  MSW
//
//  Created by simon on 09/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ContactViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIBarButtonItem *barButton;
@property (weak, nonatomic) IBOutlet UITextField *email;
@property (weak, nonatomic) IBOutlet UITextField *object;
@property (weak, nonatomic) IBOutlet UITextView *message;

- (IBAction)send:(id)sender;

@end
