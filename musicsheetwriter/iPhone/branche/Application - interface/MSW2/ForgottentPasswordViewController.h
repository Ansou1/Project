//
//  ForgottentPasswordViewController.h
//  MSW2
//
//  Created by simon on 07/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"

@interface ForgottentPasswordViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *email;
@property (strong, nonatomic) IBOutlet UIView *view2;

- (IBAction)SendEmail;
@end
