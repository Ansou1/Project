//
//  SuscribeViewController.h
//  MSW2
//
//  Created by simon on 29/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"

@interface SuscribeViewController : UIViewController

@property (weak, nonatomic) IBOutlet UITextField *pseudo;
@property (weak, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UITextField *email;

- (IBAction)valideSuscribe;
@end
