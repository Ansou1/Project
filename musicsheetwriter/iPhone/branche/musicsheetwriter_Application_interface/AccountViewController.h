//
//  MonCompteViewController.h
//  MSW2
//
//  Created by simon on 07/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"
#import "GuestMode.h"


@interface AccountViewController : UIViewController <UIActionSheetDelegate, UITextFieldDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>  {

    //photo
    UIImagePickerController *picker;
    UIImagePickerController *picker2;
    UIImage *image;
    NSString *urlPhoto;

}

@property (weak, nonatomic) IBOutlet UIBarButtonItem *barButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *searchButton;
@property (weak, nonatomic) IBOutlet UIImageView *profileImageView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UILabel *Pseudo;
@property (weak, nonatomic) IBOutlet UITextField *Name;
@property (weak, nonatomic) IBOutlet UITextField *Surname;
@property (weak, nonatomic) IBOutlet UITextField *Email;
@property (weak, nonatomic) IBOutlet UITextView *Description;
@property (weak, nonatomic) IBOutlet UITextView *Message;

//-(IBAction)hide;
-(IBAction)actionButton;
-(IBAction)validez;
-(IBAction)logout;

-(void) refresh;

-(void)TakePhoto;
-(void)ChooseExisting;
    

@end
