//
//  LoginView.h
//  MSW2
//
//  Created by simon on 22/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"

@interface LoginView : UIViewController <UITextFieldDelegate>

@property (strong, nonatomic) IBOutlet UITextField *identifiant;
@property (strong, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UIButton *connecter;
@property (strong, nonatomic) NSString *id;
@property (weak, nonatomic) IBOutlet UILabel *label;
@property (weak, nonatomic) IBOutlet UISwitch *switch1;
@property (nonatomic) BOOL firstConnection;

-(IBAction)login;

@end
